package edu.bbte.idde.ogim2385.data.dao.factory;

import edu.bbte.idde.ogim2385.data.dao.JdbcOrderDao;
import edu.bbte.idde.ogim2385.data.dao.OrderDao;
import edu.bbte.idde.ogim2385.data.jdbc.ConnectionManager;

public class JdbcDaoFactory extends AbstractDaoFactory {
    private final JdbcOrderDao orderDao = new JdbcOrderDao(ConnectionManager.getDataSource());

    @Override
    public OrderDao getOrderDao() {
        return orderDao;
    }
}
