package edu.bbte.idde.data.dao.factory;

import edu.bbte.idde.data.dao.InMemoryOrderDao;
import edu.bbte.idde.data.dao.OrderDao;

public class InMemoryDaoFactory extends AbstractDaoFactory {
    private final InMemoryOrderDao orderDao = new InMemoryOrderDao();

    @Override
    public OrderDao getOrderDao() {
        return orderDao;
    }
}
