import mountList from './listPage.js';
import mountForm from './formPage.js';

const root = document.getElementById('app');

function router(){
  const hash = location.hash || '#/items';
  const parts = hash.replace(/^#\/?/, '').split('/');
  // patterns: items | items,new | items,edit,id
  if (parts[0] === 'items'){
    if (parts[1] === 'new'){
      mountForm(root, null);
    } else if (parts[1] === 'edit' && parts[2]){
      mountForm(root, parts[2]);
    } else {
      mountList(root);
    }
  } else {
    // default
    location.hash = '#/items';
  }
}

window.addEventListener('hashchange', router);
window.addEventListener('load', router);