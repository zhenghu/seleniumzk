package fr.edf.distribution.linkylog.testing.utils;

import java.util.Properties;

/**
 * Liste des utilisateurs de test
 */
public enum User {
	/** Administrateur general */
	ADMIN,
	/** Administrateur exploitation */
	EXPLOIT,
	/** Utilisateur Avance */
	AVANCE,
	/** Utilisateur simple (profil par defaut) */
	SIMPLE,
	/** Utilisateur servant a tester la suppression */
	A_SUPPRIMER;

	/**
	 * Recupere le login de l'utilisateur dans les proprietes
	 * @param properties les proprietes, devant contenir la cle user.name
	 * @return la valeur de la cle ou valeur vide si elle n'existe pas
	 */
	public String getLogin(Properties properties) {
		return properties.getProperty(this.name().toLowerCase() + ".name");
	}

	/**
	 * Recupère le password de l'utilisateur dans les proprietes
	 * @param properties les proprietes, devant contenir la cle user.password
	 * @return la valeur de la cle ou valeur vide si elle n'existe pas
	 */
	public String getPassword(Properties properties) {
		return properties.getProperty(this.name().toLowerCase() + ".password");
	}
}
