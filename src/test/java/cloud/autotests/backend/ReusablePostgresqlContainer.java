package cloud.autotests.backend;

import org.testcontainers.containers.PostgreSQLContainer;

public class ReusablePostgresqlContainer extends PostgreSQLContainer<ReusablePostgresqlContainer> {

    private static final String IMAGE_VERSION = "postgres:12.6";

    private static ReusablePostgresqlContainer container;

    private ReusablePostgresqlContainer() {
        super(IMAGE_VERSION);
        super.withDatabaseName("autotests_cloud_db")
                .withUsername("demo_user")
                .withPassword("demo_pass");

    }

    static ReusablePostgresqlContainer getInstance() {
        if (container == null) {
            container = new ReusablePostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}