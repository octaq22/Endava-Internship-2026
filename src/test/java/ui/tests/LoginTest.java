package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    @Test
    void userCanLoginSuccessfully() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        assertTrue( homePage.isHomePageVisible(),
                "Transaction list should be visible after successful login"
        );

        assertTrue( homePage.isLogoutButtonVisible(),
                "Logout button should be visible after successful login"
        );
    }
    @Test
    void userCannotLoginWithInvalidCredentials() {
        // Verifică mesajul de eroare și faptul că loginul eșuat rămâne pe pagina de autentificare.
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);
        loginPage.openLoginPage(ConfigManager.get("base.url"));

        loginPage.enterUsername("invalid-user");
        loginPage.enterPassword("wrong-password");
        loginPage.clickLogin();

        var errorMessage = page.getByText("Incorrect username or password");
        errorMessage.waitFor();

        assertTrue(errorMessage.isVisible(),
                "An error message should appear after entering incorrect credentials");

        assertTrue(page.url().endsWith("/signin"),
                "The user should remain on the sign-in page after a failed login");
    }


}