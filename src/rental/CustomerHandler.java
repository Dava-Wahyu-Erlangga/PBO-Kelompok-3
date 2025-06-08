package rental;

public class CustomerHandler implements UserRoleHandler {
    public void openDashboard(User user) {
        new DashboardFrame(user.getId());
    }
}
