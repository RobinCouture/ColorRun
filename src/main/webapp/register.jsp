<%--
  Created by IntelliJ IDEA.
  User: robin
  Date: 14/05/2025
  Time: 12:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inscription</title>
</head>
<body>
  <div class="register-container">
    <h2>Inscription</h2>

    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="error-message">
      <%= request.getAttribute("errorMessage") %>
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/register" method="post">
      <div class="form-group">
        <label for="nom">Nom:</label>
        <input type="text" id="nom" name="nom" required>
      </div>
      <div class="form-group">
        <label for="prenom">Prénom:</label>
        <input type="text" id="prenom" name="prenom" required>
      </div>
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
      </div>
      <div class="form-group">
        <label for="motDePasse">Mot de passe:</label>
        <input type="password" id="motDePasse" name="motDePasse" required>
      </div>
      <button type="submit">S'inscrire</button>
    </form>

    <p>Déjà inscrit ? <a href="${pageContext.request.contextPath}/login">Se connecter</a></p>
  </div>
</body>
</html>
