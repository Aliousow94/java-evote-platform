-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 24 août 2026 à 15:57
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `uam_evote`
--

-- --------------------------------------------------------

--
-- Structure de la table `candidats`
--

CREATE TABLE `candidats` (
  `id` int(11) NOT NULL,
  `election_id` int(11) DEFAULT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `profession` varchar(100) DEFAULT NULL,
  `programme` text DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `candidats`
--

INSERT INTO `candidats` (`id`, `election_id`, `nom`, `prenom`, `profession`, `programme`, `photo`) VALUES
(1, 2, 'SOW', 'MAMADOU ALIOU', 'Etudiant', 'je suis etudiant en licence 3 je veus etre delegué de la sta pour veiller au respect de dhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhueu', 'C:\\Users\\mamad\\OneDrive\\Desktop\\Licence 3 Informatique\\Portfolio\\pages\\image\\imbon.jpeg'),
(2, 1, 'SARR', 'DAHIROU', 'ENSEIGNANT', 'je suui  la aujourduis pour vous presensenter ma candidature pour le poste de directeur de l\'ufr', 'C:\\Users\\mamad\\OneDrive\\Desktop\\Licence 3 Informatique\\COURS PROGRAMATIONWEB\\UFR-STA-sauvaugarde\\static\\uploads\\enseignants\\700272300_122195059106429789_3220444134498499447_n.jpg'),
(4, 1, 'SOW', 'Mensour', 'enseignan', 'je suis la pour vous presenté ma candidateur au poste de ', 'C:\\Users\\mamad\\OneDrive\\Desktop\\Licence 3 Informatique\\COURS PROGRAMATIONWEB\\UFR-STA-sauvaugarde\\static\\uploads\\enseignants\\700306103_122195059628429789_9183672534653754568_n.jpg'),
(5, 2, 'Thiam', 'Moustapha', 'Etudiant', 'jjjeeee \neekekekee', 'C:\\Users\\mamad\\OneDrive\\Desktop\\Licence 3 Informatique\\Portfolio\\pages\\image\\im3.jpeg');

-- --------------------------------------------------------

--
-- Structure de la table `elections`
--

CREATE TABLE `elections` (
  `id` int(11) NOT NULL,
  `titre` varchar(200) NOT NULL,
  `type_election` varchar(100) NOT NULL,
  `date_debut` datetime DEFAULT NULL,
  `date_fin` datetime DEFAULT NULL,
  `statut` varchar(20) DEFAULT 'En preparation',
  `electeur_role` enum('ETUDIANT','ENSEIGNANT') NOT NULL,
  `cible_ufr` varchar(50) DEFAULT NULL,
  `cible_departement` varchar(50) DEFAULT NULL,
  `cible_classe` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `elections`
--

INSERT INTO `elections` (`id`, `titre`, `type_election`, `date_debut`, `date_fin`, `statut`, `electeur_role`, `cible_ufr`, `cible_departement`, `cible_classe`) VALUES
(1, 'Election directeur', 'Directeur UFR', '2026-08-05 00:00:00', '2026-08-22 23:59:59', 'Ouverte', 'ENSEIGNANT', NULL, 'HABILITES', NULL),
(2, 'Election delegué', 'Délégués étudiants', '2026-07-31 00:00:00', '2026-08-08 23:59:59', 'Fermée', 'ETUDIANT', 'STA', NULL, NULL),
(3, 'Election Chef de departement', 'Chef de département', '2026-08-20 00:00:00', '2026-08-21 23:59:59', 'En préparation', 'ENSEIGNANT', 'STA', NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `matricule` varchar(20) NOT NULL,
  `ufr` varchar(50) DEFAULT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','ETUDIANT','ENSEIGNANT') NOT NULL DEFAULT 'ETUDIANT',
  `classe` varchar(50) DEFAULT NULL,
  `departement` varchar(50) DEFAULT NULL,
  `est_habilite` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `matricule`, `ufr`, `nom`, `prenom`, `email`, `login`, `password`, `role`, `classe`, `departement`, `est_habilite`) VALUES
(1, 'ADM001', 'TECTNA', 'Gueye', 'Amadou', 'admin@uam.edu.sn', 'admin@uam.edu.sn', '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9', 'ADMIN', ' L2 Infographie', 'Infographie', 0),
(2, 'ETU001', 'STA', 'Diop', 'Awa', 'awa.diop@uam.edu.sn', 'awa.diop@uam.edu.sn', '36432AA0A54A06C13CA2FF16CEF78CA66E1CD5FA869F36791A79BC4F4C5D8120', 'ETUDIANT', 'L3 Mathematique', 'MIM', 0),
(4, 'ETU002', 'SEGG', 'FALL', 'Aissatou', 'aissatou.fall@uam.edu.sn', 'aissatou.fall@uam.edu.sn', '9B8769A4A742959A2D0298C36FB70623F2DFACDA8436237DF08D8DFD5B37374C', 'ETUDIANT', 'L3 Contabilite', 'Contabilit', 0),
(6, 'PRO123', 'STA', 'SOW', 'DAHIROU', 'darou@uam', 'darou@uam', 'F35D1FE05E80DC73F4250B33A7AED1906EF63497AE9480995C54460DD01EF30E', 'ENSEIGNANT', '', 'MIM', 1),
(7, 'AT324', 'STA', 'ALIOU', 'SOW', 'aliou@gmail', 'aliou@gmail', '51B56DA60908E40ED7902C341ABE470663D7A2065BEE43DA0B7FDA4D9E05CDF0', 'ETUDIANT', 'L3 mathematique', 'MIM', 0),
(8, 'prof123', 'STA', 'YADE', 'Lamine', 'yade@gmail', 'yade@gmail', 'AFC1384E058925A9B30E84F711BB8CF8A6B49B81A0DF6725EEC6D5305C9FA32F', 'ENSEIGNANT', '', 'MIM', 0),
(9, 'ETU100', 'STA', 'Ba', 'Cheikh', 'cheikh.ba@uam.edu.sn', 'cheikh.ba@uam.edu.sn', 'E257D9E22CA36E4C0336ADFAA0B172DDB9D719E4301ED5E667800815EBD96BD1', 'ETUDIANT', 'L3 Informatique', 'Informatique', 0),
(10, 'ETU101', 'STA', 'Sarr', 'Aminata', 'aminata.sarr@uam.edu.sn', 'aminata.sarr@uam.edu.sn', '093033189C33A592594DB0BC46D8B0E8DA6D1079A16A4BA937D0593BFDB7DE62', 'ETUDIANT', 'L3 Informatique', 'Informatique', 0),
(11, 'ETU102', 'SEFS', 'Diouf', 'Omar', 'omar.diouf@uam.edu.sn', 'omar.diouf@uam.edu.sn', '533BECFEAB88151BAD9922C9181FC15C689BCDC5544E43F34EE28778C31E7F81', 'ETUDIANT', 'L3 Biologie', 'Biologie', 0),
(12, 'ETU103', 'SEG', 'Niang', 'Fatou', 'fatou.niang@uam.edu.sn', 'fatou.niang@uam.edu.sn', '72A09EA1C9BA7049F1E44C498675BA7026F0DFA1757DB1452E8A410201160997', 'ETUDIANT', 'L3 Gestion', 'Gestion', 0),
(13, 'ENS100', 'STA', 'Kane', 'Ibrahima', 'ibrahima.kane@uam.edu.sn', 'ibrahima.kane@uam.edu.sn', '291C0EC7B36ACA307F9DA304A09492A62EF3C8EBA9103A249014A228E3936A36', 'ENSEIGNANT', '', 'Informatique', 0),
(14, 'ENS101', 'SEFS', 'Faye', 'Mariama', 'mariama.faye@uam.edu.sn', 'mariama.faye@uam.edu.sn', 'C4D8DC7728E14CD11AE298FBF4EC4E7DD40529DBEB382369E9828351554E2AEB', 'ENSEIGNANT', '', 'Biologie', 0);

-- --------------------------------------------------------

--
-- Structure de la table `votes`
--

CREATE TABLE `votes` (
  `id` int(11) NOT NULL,
  `election_id` int(11) DEFAULT NULL,
  `candidat_id` int(11) DEFAULT NULL,
  `utilisateur_id` int(11) DEFAULT NULL,
  `date_vote` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `votes`
--

INSERT INTO `votes` (`id`, `election_id`, `candidat_id`, `utilisateur_id`, `date_vote`) VALUES
(1, 2, 1, 2, '2026-08-04 10:14:29'),
(2, 1, 2, 6, '2026-08-04 11:07:14'),
(3, 2, 5, 7, '2026-08-04 11:24:44'),
(4, 1, 2, 8, '2026-08-04 11:53:47'),
(5, 2, 5, 9, '2026-08-05 12:24:07');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD PRIMARY KEY (`id`),
  ADD KEY `election_id` (`election_id`);

--
-- Index pour la table `elections`
--
ALTER TABLE `elections`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `matricule` (`matricule`),
  ADD UNIQUE KEY `login` (`login`);

--
-- Index pour la table `votes`
--
ALTER TABLE `votes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `election_id` (`election_id`,`utilisateur_id`),
  ADD KEY `candidat_id` (`candidat_id`),
  ADD KEY `utilisateur_id` (`utilisateur_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `candidats`
--
ALTER TABLE `candidats`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `elections`
--
ALTER TABLE `elections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT pour la table `votes`
--
ALTER TABLE `votes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD CONSTRAINT `candidats_ibfk_1` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `votes`
--
ALTER TABLE `votes`
  ADD CONSTRAINT `votes_ibfk_1` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_ibfk_2` FOREIGN KEY (`candidat_id`) REFERENCES `candidats` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_ibfk_3` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
