package fr.esgi.robin.colorrun.util;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import com.google.gson.GsonBuilder;
import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.FilsDiscussion;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.FilsDiscussionRepository;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
import fr.esgi.robin.colorrun.repository.impl.FilsDiscussionRepositoryImpl;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ServerEndpoint("/chat/{courseId}")
public class ChatWebSocket {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Gson gson = createGson();

    private static final FilsDiscussionRepository discussionRepository = new FilsDiscussionRepositoryImpl();
    private static final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();
    private static final CoursesRepository courseRepository = new CoursesRepositoryImpl();

    @OnOpen
    public void onOpen(Session session, @PathParam("courseId") String courseId) {
        session.getUserProperties().put("courseId", courseId);

        // Récupérer le nom d'utilisateur à partir des paramètres de la requête
        Map<String, List<String>> parameters = session.getRequestParameterMap();
        String username = "Anonyme";
        Integer userId = -1;
        if (parameters.containsKey("username")) {
            username = parameters.get("username").get(0);
        }

        if (parameters.containsKey("userId")) {
            userId = Integer.parseInt(parameters.get("userId").get(0));
        }

        // Stocker le nom d'utilisateur et l'id utilisateur dans les propriétés de la session
        session.getUserProperties().put("username", username);
        session.getUserProperties().put("userId", userId);

        // Envoyer l'historique des messages à l'utilisateur qui vient de se connecter
        List<FilsDiscussion> historique = discussionRepository.findByCourseId(Integer.parseInt(courseId));
        if (!historique.isEmpty()) {
            for (FilsDiscussion message : historique) {
                try {
                    JsonObject simpleMessage = new JsonObject();
                    simpleMessage.addProperty("username", message.getUtilisateur().getNom());
                    simpleMessage.addProperty("content", message.getContenu());
                    simpleMessage.addProperty("timestamp", message.getDateEnvoi().toEpochMilli());

                    session.getBasicRemote().sendText(gson.toJson(simpleMessage));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        sessions.add(session);
        System.out.println("Nouvelle connexion au chat de la course " + courseId + " par " + username);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        String courseId = (String) session.getUserProperties().get("courseId");
        String username = (String) session.getUserProperties().get("username");
        Integer userId = (Integer) session.getUserProperties().get("userId");
        Map<String, Object> test = session.getUserProperties();
        String testa = "totezot";

        if (username == null) {
            username = "Anonyme";
        }

        // Traiter le message JSON reçu
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        // Utiliser le nom stocké dans la session plutôt que celui du message
        String content = jsonMessage.get("content").getAsString();

        // Créer le message à envoyer
        JsonObject outMessage = new JsonObject();
        outMessage.addProperty("username", username);
        outMessage.addProperty("content", content);
        outMessage.addProperty("timestamp", System.currentTimeMillis());

        String outMessageStr = gson.toJson(outMessage);

        Utilisateur user = utilisateurRepository.findById(userId);
        Courses courses = courseRepository.findById(Integer.parseInt(courseId));

        // Enregistrer le message dans la base de données
        FilsDiscussion newMessage = new FilsDiscussion(null, content, new Date().toInstant(), user, courses);
        discussionRepository.save(newMessage);

        // Envoyer à tous les utilisateurs de la même course
        synchronized(sessions) {
            sessions.forEach(s -> {
                if (courseId.equals(s.getUserProperties().get("courseId"))) {
                    try {
                        s.getBasicRemote().sendText(outMessageStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String username = (String) session.getUserProperties().get("username");
        String courseId = (String) session.getUserProperties().get("courseId");
        System.out.println("Session WebSocket fermée: ID=" + session.getId() +
                ", Utilisateur=" + username +
                ", Course=" + courseId +
                ", Raison=" + closeReason.getReasonPhrase() +
                ", Code=" + closeReason.getCloseCode());
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        System.err.println("Erreur WebSocket pour la session " + error.getMessage());
        error.printStackTrace();
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantAdapter());
        return gsonBuilder.create();
    }
}