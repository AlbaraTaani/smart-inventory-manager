import mountList from './listPage.js';
import mountForm from './formPage.js';

const root = document.getElementById('app');

function router(){
  const hash = location.hash || '#/items';
  const parts = hash.replace(/^#\/?/, '').split('/');
  if (parts[0] === 'items'){
    if (parts[1] === 'new'){
      mountForm(root, null);
    } else if (parts[1] === 'edit' && parts[2]){
      mountForm(root, parts[2]);
    } else {
      mountList(root);
    }
  } else {
    location.hash = '#/items';
  }
}

window.addEventListener('hashchange', router);
window.addEventListener('load', router);