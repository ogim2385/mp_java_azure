package edu.bbte.idde.data.dao.factory;

import edu.bbte.idde.config.ApplicationConfig;
import edu.bbte.idde.config.ConfigLoader;
import edu.bbte.idde.data.dao.OrderDao;
import edu.bbte.idde.data.exception.ConfigLoadException;

public abstract class AbstractDaoFactory {

    public abstract OrderDao getOrderDao();

    public static AbstractDaoFactory getFactory() throws ConfigLoadException {
        ApplicationConfig config = ConfigLoader.load();
        String profile = config.getProfile();

        if ("jdbc".equalsIgnoreCase(profile)) {
            return new JdbcDaoFactory();
        } else {
            return new InMemoryDaoFactory();
        }
    }
}
