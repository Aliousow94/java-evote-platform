# Système de Vote Électronique Local (UAM)

Ce dépôt contient le projet final de l'unité d'enseignement **Java Avancé** réalisé en L3 Informatique à l'Université Amadou Mahtar Mbow (UAM), sous la direction du Pr. Amadou Dahirou GUEYE et du Dr Lamine YADE.

L'objectif de l'application est de fournir une plateforme souveraine, locale et sécurisée pour l'organisation des scrutins internes de l'université (élections des délégués, chefs de département, directeurs d'UFR).

---

## 🚀 Fonctionnalités Clés

* **Architecture robuste :** Implémentation du pattern architectural **MVC** (Modèle-Vue-Contrôleur) pour découpler la logique métier de l'affichage.
* **Sécurité & Anti-fraude :** Chiffrement des mots de passe en **SHA-256**, mécanisme d'authentification par rôles, et contraintes d'unicité SQL en base de données pour empêcher le double vote.
* **Ciblage dynamique :** Filtrage automatique des scrutins selon le profil de l'électeur connecté (UFR, Département, Classe).
* **Tableau de bord Administrateur :** Statistiques en temps réel avec des visualisations graphiques dynamiques (BarChart, PieChart JavaFX).
* **Fonctionnalités Bonus :** Importation de listes d'électeurs en masse via fichiers CSV et génération automatique de cartes d'électeurs ou de rapports de dépouillement officiels au format PDF (iText).

---

## 🛠️ Technologies & Outils Utilisés

* **Langage :** Java 17
* **Interface Graphique :** JavaFX, FXML, Scene Builder, CSS personnalisé
* **Base de Données :** MySQL (Pilote JDBC)
* **Gestionnaire de dépendances :** Maven
