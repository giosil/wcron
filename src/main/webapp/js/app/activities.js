var _table   = new TableActivities('#tabResult');
var _btnAdd  = $('#btnAdd');
var _dialog  = $('#dlgEdit');
var _name    = $('#name');
var _uri     = $('#uri');
var _params  = $('#parameters');
var _btnSave = $('#btnSave');

// Functions

function _initPageApp(){
  
  _btnAdd.on('click', function(e){
    doAdd();
  });
  
  _btnSave.on('click', function(e){
    doSave();
  });
  
  _dialog.on('shown.bs.modal', function(e){
    onDlgShow();
  });
  
  _table.onDblClick = function(e) {
    doEdit(_table.selIndex);
  }
  
  loadData();
  
}

function loadData(){
  $.ajax({
    type: "GET",
    url: "/wcron/scheduler/manager/listActivities"
  }).then(function(data){
    _table.setData(data);
  }).fail(function() {
    alert('An error has occurred.');
  });
}

function reload(){
  setTimeout(function(){ loadData(); }, 100);
}

function onDlgShow(){
  var dlgop = _dialog.data('op');
  if(dlgop == 'edit') {
    _uri.focus();
  }
  else {
    _name.focus();
  }
}

function doAdd(){
  _name.val('');
  _uri.val('');
  _params.val('');
  
  _name.prop('disabled', false);
  
  _dialog.data('op','add');
  _dialog.modal('show');
}

function doEdit(i){
  if(i < 0) return;
  var r=_table.records[i];
  _name.val(r['name']);
  _uri.val(r['uri']);
  _params.val(_ojbToJson(r['parameters']));
  
  _name.prop('disabled', true);
  
  _dialog.data('op','edit');
  _dialog.modal('show');
}

function doRemove(i){
  var c=confirm('Are you sure to remove the activity?');
  if(!c) return;
  
  var r=_table.records[i];
  
  $.ajax({
    type: "GET",
    url: "/wcron/scheduler/manager/removeActivity/" + r['name']
  }).then(function(res){
    if(!res) {
      alert('Job not removed.');
      return;
    }
    reload();
  }).fail(function() {
    alert('An error has occurred.');
  });
}

function doSave(){
  var dlgop  = _dialog.data('op');
  var name   = _name.val();
  var uri    = _uri.val();
  var params = _params.val();
  
  console.log('op=' + dlgop + ',name=' + name + ',uri=' + uri);
  
  if(!name) {
    alert('Invalid name');
    _name.focus();
    return;
  }
  if(!uri) {
    alert('Invalid URI');
    _uri.focus();
    return;
  }
  
  $.ajax({
    type: "POST",
    url: "/wcron/scheduler/manager/addActivity",
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    data: JSON.stringify({"name":name, "uri":uri, "parameters":_jsonToObj(params)})
  }).then(function(res){
    if(!res) {
      alert('Activity not added.');
      return;
    }
    reload();
  }).fail(function(){
    alert('An error has occurred.');
  });
  
  _dialog.modal('hide');
}

// GUI Objects

function TableActivities(id){
  this.records=[];
  this.idtable=id;
  this.selIndex=-1;
  var _this=this;
  
  $(this.idtable).on('click', 'tbody tr', function(e){
    $(this).addClass('success').siblings().removeClass('success');
    var index=$(this).index();
    if(_this.selIndex!=index){
      _this.selIndex=index;
      _this.onSelection(e);
    }
  });
  $(this.idtable).on('dblclick', 'tbody tr', function(e){
    _this.selIndex=$(this).index();
    _this.onDblClick(e);
  });
}
TableActivities.prototype.onSelection=function(e){
  console.log('onSelection selIndex=' + this.selIndex);
}
TableActivities.prototype.onDblClick=function(e){
  console.log('onDblClick selIndex=' + this.selIndex);
}
TableActivities.prototype.clear=function(){
  this.records=[];
  this.selIndex=-1;
  $(this.idtable+' tbody').html('');
}
TableActivities.prototype.clearSelection=function(){
  this.selIndex=-1;
  $(this.idtable+' tbody tr').removeClass('success');
}
TableActivities.prototype.setData=function(data){
  if(!$.isArray(data)){
    this.records=[];
    $(this.idtable+' tbody').html('');
    return;
  }
  this.records=data;
  this.refresh();
}
TableActivities.prototype.refresh=function(){
  var rows='';
  for(var i=0;i<this.records.length;i++){
    var r=this.records[i];
    rows+='<tr>';
    rows+='<td>'+r['name']+'</td>';
    rows+='<td>'+r['uri']+'</td>';
    rows+='<td>'+_formatDateTime(r['createdAt'])+'</td>';
    // Row actions
    rows+='<td><div class="btn-group">';
    rows+='<button class="btn btn-xs btn-default" style="margin-right: 4px;" onclick="doEdit(' + i + ')">Edit</button>';
    rows+='<button class="btn btn-xs btn-default" style="margin-right: 4px;" onclick="doRemove(' + i + ')">Remove</button>';
    rows+='</div></td>'; 
    rows+='</tr>';
  }
  $(this.idtable+' tbody').html(rows);
}

