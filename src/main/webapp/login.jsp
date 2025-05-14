<%--
  Created by IntelliJ IDEA.
  User: robin
  Date: 14/05/2025
  Time: 12:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Connexion</title>
</head>
<body>
  <div class="login-container">
    <h2>Connexion</h2>

    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="error-message">
      <%= request.getAttribute("errorMessage") %>
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login" method="post">
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
      </div>
      <div class="form-group">
        <label for="motDePasse">Mot de passe:</label>
        <input type="password" id="motDePasse" name="motDePasse" required>
      </div>
      <button type="submit">Se connecter</button>
    </form>

    <p>Pas encore inscrit ? <a href="${pageContext.request.contextPath}/register">Créer un compte</a></p>
  </div>
</body>
</html>
