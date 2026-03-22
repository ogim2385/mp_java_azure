package edu.bbte.idde.ogim2385.data.dao.factory;

import edu.bbte.idde.ogim2385.data.dao.InMemoryOrderDao;
import edu.bbte.idde.ogim2385.data.dao.OrderDao;

public class InMemoryDaoFactory extends AbstractDaoFactory {
    private final InMemoryOrderDao orderDao = new InMemoryOrderDao();

    @Override
    public OrderDao getOrderDao() {
        return orderDao;
    }
}
