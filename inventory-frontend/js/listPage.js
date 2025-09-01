import {getAllItems, getLowStockItems, deleteItem} from './api.js';

export default function mountList(container){
  container.innerHTML = '';

  const controls = document.createElement('div');
  controls.className = 'controls';

  const lowStockBtn = document.createElement('button');
  lowStockBtn.className = 'btn';
  lowStockBtn.textContent = 'Show Low-stock';

  // Threshold input (default 5) — user can change to 1,2,3,...
  const thresholdInput = document.createElement('input');
  thresholdInput.type = 'number';
  thresholdInput.min = '0';
  thresholdInput.step = '1';
  thresholdInput.value = '5';
  thresholdInput.title = 'Low-stock threshold (default 5)';

  const minInput = document.createElement('input');
  minInput.placeholder = 'Min price';
  minInput.type = 'number';
  minInput.step = '0.01';

  const maxInput = document.createElement('input');
  maxInput.placeholder = 'Max price';
  maxInput.type = 'number';
  maxInput.step = '0.01';

  const filterBtn = document.createElement('button');
  filterBtn.className = 'btn';
  filterBtn.textContent = 'Apply Price Filter';

  const sortBtn = document.createElement('button');
  sortBtn.className = 'btn';
  sortBtn.textContent = 'Sort by price ↑';
  sortBtn.dataset.order = 'asc';

  controls.append(lowStockBtn, thresholdInput, minInput, maxInput, filterBtn, sortBtn);
  container.appendChild(controls);

  const message = document.createElement('div');
  container.appendChild(message);

  const table = document.createElement('table');
  table.innerHTML = `
    <thead>
      <tr><th>Name</th><th>Quantity</th><th>Price</th><th>Actions</th></tr>
    </thead>
    <tbody></tbody>
  `;
  container.appendChild(table);

  let lowMode = false;

  async function load(){
    message.textContent = '';
    const tbody = table.querySelector('tbody');
    tbody.innerHTML = '';
    try{
      let items;
      if (lowMode){
        // Use user-provided threshold; fallback to 5 if invalid
        const raw = thresholdInput.value;
        let threshold = Number(raw);
        if (Number.isNaN(threshold) || threshold < 0) threshold = 5;
        items = await getLowStockItems(threshold);
      } else {
        const minPrice = minInput.value ? Number(minInput.value) : undefined;
        const maxPrice = maxInput.value ? Number(maxInput.value) : undefined;
        const order = sortBtn.dataset.order === 'asc' ? 'asc' : 'desc';
        items = await getAllItems({minPrice, maxPrice, sortBy: 'price', order});
      }
      if (!items || items.length === 0){
        message.innerHTML = '<div class="small">No items found</div>';
        return;
      }
      for (const it of items){
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>${escapeHtml(it.name)}</td>
          <td>${it.quantity}</td>
          <td>${it.price}</td>
          <td>
            <button class="btn" data-id="${it.id}" data-action="edit">Edit</button>
            <button class="btn btn-danger" data-id="${it.id}" data-action="delete">Delete</button>
          </td>
        `;
        tbody.appendChild(tr);
      }
    } catch (err){
      message.innerHTML = `<div class="error">${escapeHtml(err.message || 'Failed to load')}</div>`;
    }
  }

  controls.addEventListener('click', async (e) => {
    const t = e.target;
    if (t === lowStockBtn){
      lowMode = !lowMode;
      lowStockBtn.textContent = lowMode ? 'Show All' : 'Show Low-stock';
      // If toggling on, immediately load low stock with the threshold currently set
      await load();
    } else if (t === filterBtn){
      lowMode = false;
      lowStockBtn.textContent = 'Show Low-stock';
      await load();
    } else if (t === sortBtn){
      sortBtn.dataset.order = sortBtn.dataset.order === 'asc' ? 'desc' : 'asc';
      sortBtn.textContent = sortBtn.dataset.order === 'asc' ? 'Sort by price ↑' : 'Sort by price ↓';
      await load();
    }
  });

  // If threshold is changed while lowMode is active, reload automatically
  thresholdInput.addEventListener('change', async () => {
    if (lowMode) await load();
  });

  table.addEventListener('click', async (e) =>{
    const btn = e.target.closest('button');
    if (!btn) return;
    const id = btn.dataset.id;
    const action = btn.dataset.action;
    if (action === 'edit'){
      location.hash = `#/items/edit/${id}`;
    } else if (action === 'delete'){
      if (!confirm('Delete this item?')) return;
      try{
        await deleteItem(id);
        await load();
      } catch (err){
        message.innerHTML = `<div class="error">${escapeHtml(err.message || 'Delete failed')}</div>`;
      }
    }
  });

  load();
}

function escapeHtml(s){
  if (!s && s !== 0) return '';
  return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;');
}