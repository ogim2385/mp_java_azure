import { useState, useEffect } from 'react';
import './App.css';

const API = 'https://mp-db-orban-gergo-embvb5efagg2ggbe.spaincentral-01.azurewebsites.net/orders';

function App() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState(null);
  const [messageType, setMessageType] = useState('');
  const [form, setForm] = useState({
    orderDate: '',
    price: '',
    status: 'PENDING',
    shippingAddress: ''
  });

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    loadOrders();
  }, []);

  function showMessage(text, type) {
    setMessage(text);
    setMessageType(type);
    setTimeout(() => setMessage(null), 3000);
  }

  function loadOrders() {
    fetch(API)
        .then(res => res.json())
        .then(data => setOrders(data))
        .catch(() => showMessage('Error loading orders', 'error'));
  }

  function createOrder() {
    if (!form.orderDate || !form.price || !form.shippingAddress) {
      showMessage('Fill in all fields!', 'error');
      return;
    }

    fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        orderDate: form.orderDate,
        price: parseFloat(form.price),
        status: form.status,
        shippingAddress: form.shippingAddress
      })
    })
        .then(res => {
          if (res.ok) {
            showMessage('Order created!', 'success');
            loadOrders();
            setForm({ orderDate: '', price: '', status: 'PENDING', shippingAddress: '' });
          } else {
            showMessage('Error creating order', 'error');
          }
        })
        .catch(() => showMessage('Error creating order', 'error'));
  }

  function updateStatus(id) {
    const newStatus = prompt('New status (PENDING, SHIPPED, DELIVERED, CANCELLED):');
    if (!newStatus) return;

    fetch(API + '?id=' + id, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        orderDate: '2026-01-01',
        price: 0,
        status: newStatus,
        shippingAddress: 'placeholder'
      })
    })
        .then(res => {
          if (res.ok) {
            showMessage('Status updated!', 'success');
            loadOrders();
          } else {
            showMessage('Error updating', 'error');
          }
        })
        .catch(() => showMessage('Error updating', 'error'));
  }

  function deleteOrder(id) {
    if (!window.confirm('Delete order ' + id + '?')) return;

    fetch(API + '?id=' + id, { method: 'DELETE' })
        .then(res => {
          if (res.ok || res.status === 204) {
            showMessage('Order deleted!', 'success');
            loadOrders();
          } else {
            showMessage('Error deleting', 'error');
          }
        })
        .catch(() => showMessage('Error deleting', 'error'));
  }

  return (
      <div style={{ maxWidth: 900, margin: '0 auto', padding: 20, fontFamily: 'Arial' }}>
        <h1>Orders Manager</h1>

        {message && (
            <div style={{
              padding: 10,
              marginBottom: 10,
              borderRadius: 4,
              background: messageType === 'success' ? '#dff0d8' : '#f2dede',
              color: messageType === 'success' ? '#3c763d' : '#a94442'
            }}>
              {message}
            </div>
        )}

        <h2>Create New Order</h2>
        <div style={{ background: 'white', padding: 20, borderRadius: 8, marginBottom: 20 }}>
          <div style={{ marginBottom: 10 }}>
            <label>Date: </label>
            <input type="date" value={form.orderDate}
                   onChange={e => setForm({...form, orderDate: e.target.value})} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <label>Price: </label>
            <input type="number" step="0.01" value={form.price}
                   onChange={e => setForm({...form, price: e.target.value})} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <label>Status: </label>
            <select value={form.status}
                    onChange={e => setForm({...form, status: e.target.value})}>
              <option>PENDING</option>
              <option>SHIPPED</option>
              <option>DELIVERED</option>
              <option>CANCELLED</option>
            </select>
          </div>
          <div style={{ marginBottom: 10 }}>
            <label>Address: </label>
            <input type="text" value={form.shippingAddress}
                   onChange={e => setForm({...form, shippingAddress: e.target.value})} />
          </div>
          <button onClick={createOrder}
                  style={{ background: '#4CAF50', color: 'white', padding: '8px 16px', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            Create Order
          </button>
        </div>

        <h2>All Orders</h2>
        <button onClick={loadOrders}
                style={{ background: '#2196F3', color: 'white', padding: '8px 16px', border: 'none', borderRadius: 4, cursor: 'pointer', marginBottom: 10 }}>
          Refresh
        </button>

        <table style={{ width: '100%', borderCollapse: 'collapse', background: 'white' }}>
          <thead>
          <tr style={{ background: '#4CAF50', color: 'white' }}>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>ID</th>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>Date</th>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>Price</th>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>Status</th>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>Address</th>
            <th style={{ padding: 10, border: '1px solid #ddd' }}>Actions</th>
          </tr>
          </thead>
          <tbody>
          {orders.length === 0 ? (
              <tr><td colSpan="6" style={{ padding: 10, textAlign: 'center' }}>No orders</td></tr>
          ) : (
              orders.map(order => (
                  <tr key={order.id}>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>{order.id}</td>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>{order.orderDate}</td>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>{order.price}</td>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>{order.status}</td>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>{order.shippingAddress}</td>
                    <td style={{ padding: 10, border: '1px solid #ddd' }}>
                      <button onClick={() => updateStatus(order.id)}
                              style={{ background: '#2196F3', color: 'white', border: 'none', padding: '4px 8px', borderRadius: 4, cursor: 'pointer', marginRight: 4 }}>
                        Update
                      </button>
                      <button onClick={() => deleteOrder(order.id)}
                              style={{ background: '#f44336', color: 'white', border: 'none', padding: '4px 8px', borderRadius: 4, cursor: 'pointer' }}>
                        Delete
                      </button>
                    </td>
                  </tr>
              ))
          )}
          </tbody>
        </table>
      </div>
  );
}

export default App;