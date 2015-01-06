package fr.edf.distribution.linkylog.testing.ihm.admin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import fr.edf.distribution.linkylog.testing.ihm.AbstractIhmTest;
import fr.edf.distribution.linkylog.testing.utils.User;

/**
 * Test fonctionnel de l'écran Admin > Utilisateurs
 */
public class UserTest extends AbstractIhmTest {

	/** NNI utilise pour tous les tests de recherche OK */
	private String nni;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		nni = User.AVANCE.getLogin(properties);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test_affichage_admin() throws Exception {
		login(User.ADMIN);

		loadPage();

		// les boutons supprimer et modifier sont affiches et actives
		assertTrue(findElementById("updateButton_button").isDisplayed() && findElementById("updateButton_button").isEnabled());
		assertTrue(findElementById("deleteButton_button").isDisplayed() && findElementById("deleteButton_button").isEnabled());

		testRecherche();
	}

	@Test
	public void test_affichage_exploit() throws Exception {
		login(User.EXPLOIT);

		loadPage();

		// on verifie que le bouton supprimer est masqué et le bouton Modifier affiche et actif
		assertTrue(findElementById("updateButton_button").isDisplayed() && findElementById("updateButton_button").isEnabled());
		assertFalse(findElementById("deleteButton_button").isDisplayed());

		testRecherche();
	}

	private void loadPage() throws Exception {
		// chargement de l'écran
		findElementById("adminMenu_menu-b").click();
		findElementById("userListMenuitem_menuitem-a").click();
		String expectedUrl = base_url + "/sup/#UserMD";
		waitForPageLoaded(expectedUrl);
		Assertions.assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
	}

	private void testRecherche() {
		// recherche d'un utilisateur
		writeValueInTextbox("filterCodeTextbox", nni);
		clickButton("searchButton");

		// Controle d'affichage dans la liste
		assertTextValue(nni, waitForCellInListbox("resultListbox", 1, 1));

		// Recherche d'un NNI inexistant et controle que la liste de resultats est vide
		writeValueInTextbox("filterCodeTextbox", "test1234");
		clickButton("searchButton");
		waitForListboxEmpty("resultListbox");

		// Recherche d'un profil
		clearTextBox("filterCodeTextbox");
		Select profils = new Select(findElementById("filterProfilSelectbox_selectbox"));
		profils.selectByIndex(4); // profil Simple (on est certain d'avoir des utilisateurs de ce profil)
		clickButton("searchButton");
		// on controle juste qu'on a au moins 1 résultat qui s'affiche
		waitForCellInListbox("resultListbox", 1, 1);

		// recherche par NNI+Profil qui n'existe pas
		// le profil selectionne reste Simple
		writeValueInTextbox("filterCodeTextbox", nni);
		clickButton("searchButton");
		waitForListboxEmpty("resultListbox");

		// recherche par NI+Profil qui existe
		// Le NNI reste celui de AVANCE
		profils.selectByIndex(3); // Avance
		clickButton("searchButton");
		assertTextValue(nni, waitForCellInListbox("resultListbox", 1, 1));

		// recherche sans critères
		clearTextBox("filterCodeTextbox");
		profils.selectByIndex(0); //vide
		clickButton("searchButton");
		// on controle juste qu'on a au moins 1 résultat qui s'affiche
		waitForCellInListbox("resultListbox", 1, 1);
	}

	// TODO tests
	// affichage du détail d'un utilisateur
	// modification de l'utilisateur
	// modification de l'utilisateur courant
	// suppression d'un utilisateur

	// Exploitant
	// modification de l'utilisateur admin
	// modification de l'utilisateur non admin vers admin
	// modification de l'utilisateur non admin vers autre role non admin
}
