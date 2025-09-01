import {createItem, getItemById, updateItem} from './api.js';

export default function mountForm(container, id){
  container.innerHTML = '';
  const isEdit = !!id;

  const form = document.createElement('form');
  form.innerHTML = `
    <div class="form-group">
      <label for="name">Name</label>
      <input id="name" name="name" type="text" />
    </div>
    <div class="form-group">
      <label for="description">Description</label>
      <input id="description" name="description" type="text" />
    </div>
    <div class="form-group">
      <label for="quantity">Quantity</label>
      <input id="quantity" name="quantity" type="number" min="0" />
    </div>
    <div class="form-group">
      <label for="price">Price</label>
      <input id="price" name="price" type="number" step="0.01" min="0" />
    </div>
    <div>
      <button class="btn" type="submit">${isEdit ? 'Update' : 'Create'}</button>
      <button class="btn" type="button" id="cancel">Cancel</button>
    </div>
  `;

  const message = document.createElement('div');
  container.append(message, form);

  async function load(){
    if (!isEdit) return;
    try{
      const dto = await getItemById(id);
      form.name.value = dto.name || '';
      form.description.value = dto.description || '';
      form.quantity.value = dto.quantity != null ? dto.quantity : '';
      form.price.value = dto.price != null ? dto.price : '';
    } catch (err){
      message.innerHTML = `<div class="error">${escapeHtml(err.message||'Failed to load item')}</div>`;
    }
  }

  form.addEventListener('submit', async (e) =>{
    e.preventDefault();
    message.innerHTML = '';
    const dto = {
      name: form.name.value.trim(),
      description: form.description.value.trim(),
      quantity: form.quantity.value === '' ? null : Number(form.quantity.value),
      price: form.price.value === '' ? null : Number(form.price.value)
    };

    // client-side validation
    const errs = [];
    if (!dto.name) errs.push('Name is required');
    if (dto.quantity == null || Number.isNaN(dto.quantity)) errs.push('Quantity is required');
    else if (!Number.isInteger(dto.quantity)) errs.push('Quantity must be an integer');
    else if (dto.quantity < 0) errs.push('Quantity must be >= 0');
    if (dto.price == null || Number.isNaN(dto.price)) errs.push('Price is required');
    else if (dto.price < 0) errs.push('Price must be >= 0');

    if (errs.length){
      message.innerHTML = `<div class="error">${escapeHtml(errs.join('; '))}</div>`;
      return;
    }

    try{
      if (isEdit){
        await updateItem(id, dto);
        location.hash = '#/items';
      } else {
        await createItem(dto);
        location.hash = '#/items';
      }
    } catch (err){
      // err.body may contain ErrorResponse
      const msg = err.body && err.body.message ? err.body.message : err.message;
      message.innerHTML = `<div class="error">${escapeHtml(msg || 'Request failed')}</div>`;
    }

  });

  form.querySelector('#cancel').addEventListener('click', ()=> location.hash = '#/items');

  load();
}

function escapeHtml(s){
  if (!s && s !== 0) return '';
  return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;');
}