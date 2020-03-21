// Common utilities

function _formatDateTime(v){
  if(v == null || v == 0) return '';
  if(typeof v == 'string') return v;
  var d = '';
  if(v instanceof Date){
    d = v;
  }
  else if(typeof v == 'number'){
    d = new Date(v);
  }
  return d.toLocaleString();
}
function _ojbToJson(obj) {
  if(obj == null) return '';
  if(typeof obj == 'object') {
    return JSON.stringify(obj);
  }
  return '';
}
function _jsonToObj(json){
  if(json == null) return {};
  json = json.trim();
  if(json.length < 7 && json.charAt(0) != '{') return {};
  var obj = {};
  try { 
    obj = JSON.parse(json); 
  }
  catch(err){ 
    console.error('Invalid object: ' + json); 
  }
  return obj;
}
function _getUrlParam(p){
  var u=decodeURIComponent(window.location.search.substring(1)),v=u.split('&');
  for(var i=0;i<v.length;i++){
    var kv=v[i].split('=');
    if(kv[0]==p) return kv[1]===undefined?true:kv[1];
  }
}

function _setFormValues(form,data){
  $.each(data, function(k,v){
    var c=$('[name='+k+']',form);
    if(c==null||!c.length)c=$('#'+k,form);
    if(c==null||!c.length)return;
    if(v==null) v='';
    switch(c.attr("type")){
      case 'checkbox':
      case 'radio':
        c.attr("checked",v);
        break;
      default:
        if(v instanceof Date){
          c.val($.datepicker.formatDate('dd/mm/yy',v));
        }
        else if($.isArray(v)){
          c.val(v);
        }
        else{
          c.val(v.toString());
        }
    }
  });
}
function _setFormValue(form,k,v){
  var c=$('[name='+k+']',form);
  if(c==null||!c.length)c=$('#'+k,form);
  if(c==null||!c.length)return;
  if(v==null)v="";
  switch(c.attr("type")){
  case 'checkbox':
  case 'radio':
    c.attr("checked",v);
    break;
  default:
    if(v instanceof Date){
      c.val($.datepicker.formatDate('dd/mm/yy',v));
    }
    else if($.isArray(v)){
      c.val(v);
    }
    else{
      c.val(v.toString());
    }
  }
}
function _getFormValues(form){
  var r={}, f=(form instanceof jQuery)?form[0]:form;
  if(f==null)return r;
  for(var i=0;i<f.elements.length;i++){
    var e=f.elements[i];
    if(!e.name||!e.value) continue;
    var k=e.name;
    switch(e.type){
    case 'checkbox':
      r[k]=e.checked;
      break;
    case 'radio':
      if(e.checked)r[k]=e.value;
      break;
    case 'select-one':
      r[k]=e.options[e.selectedIndex].value;
      break;
    case 'select-multiple':
      var a=[];
      for(var j=0;j<e.length;j++){
        if(e.options[j].selected){
          a.push(e.options[j].value);
        }
      }
      r[k]=a;
      break;
    case 'text':
    case 'textarea':
      r[k]=e.value;
      break;
    default:
      r[k]=e.value;
    }
  }
  return r;
}