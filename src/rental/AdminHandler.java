package rental;

public class AdminHandler implements UserRoleHandler {
    public void openDashboard(User user) {
        new AdminDashboard();
    }
}
