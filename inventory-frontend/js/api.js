const API_BASE = 'http://localhost:8080/api/items';

async function request(url, opts = {}){
  const res = await fetch(url, opts);
  const contentType = res.headers.get('content-type') || '';
  let body = null;
  if (contentType.includes('application/json')){
    body = await res.json();
  } else {
    body = await res.text();
  }
  if (!res.ok){
    const msg = body && body.message ? body.message : (typeof body === 'string' ? body : 'Unknown error');
    const error = new Error(msg);
    error.status = res.status;
    error.body = body;
    throw error;
  }
  return body;
}

export async function getAllItems({minPrice, maxPrice, sortBy, order} = {}){
  const params = new URLSearchParams();
  if (minPrice != null) params.set('minPrice', String(minPrice));
  if (maxPrice != null) params.set('maxPrice', String(maxPrice));
  if (sortBy) params.set('sortBy', sortBy);
  if (order) params.set('order', order);
  const url = `${API_BASE}${params.toString() ? '?'+params.toString() : ''}`;
  return request(url);
}

export async function getLowStockItems(threshold = 5){
  return request(`${API_BASE}/low-stock?threshold=${encodeURIComponent(threshold)}`);
}

export async function getItemById(id){
  return request(`${API_BASE}/${id}`);
}

export async function createItem(dto){
  return request(API_BASE, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(dto)
  });
}

export async function updateItem(id, dto){
  return request(`${API_BASE}/${id}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(dto)
  });
}

export async function deleteItem(id){
  return request(`${API_BASE}/${id}`, {method: 'DELETE'});
}