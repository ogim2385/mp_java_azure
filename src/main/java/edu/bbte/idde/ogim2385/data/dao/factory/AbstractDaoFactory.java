package edu.bbte.idde.ogim2385.data.dao.factory;

import edu.bbte.idde.ogim2385.data.dao.OrderDao;

public abstract class AbstractDaoFactory {
    public enum FactoryType {
        IN_MEMORY, JDBC
    }

    public abstract OrderDao getOrderDao();

    public static AbstractDaoFactory getFactory(FactoryType type) {
        switch (type) {
            case IN_MEMORY:
                return new InMemoryDaoFactory();
            case JDBC:
                return new JdbcDaoFactory();
            default:
                throw new IllegalArgumentException("Unsupported factory type");
        }
    }
}
