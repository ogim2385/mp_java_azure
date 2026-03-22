package edu.bbte.idde.data.dao.factory;

import edu.bbte.idde.data.dao.JdbcOrderDao;
import edu.bbte.idde.data.dao.OrderDao;
import edu.bbte.idde.data.jdbc.ConnectionManager;

public class JdbcDaoFactory extends AbstractDaoFactory {
    private final JdbcOrderDao orderDao = new JdbcOrderDao(ConnectionManager.getDataSource());

    @Override
    public OrderDao getOrderDao() {
        return orderDao;
    }
}
