var _table = new TableJobs('#tabResult');
var _btnAdd     = $('#btnAdd');
var _btnReload  = $('#btnReload');
var _dialog     = $('#dlgEdit');
var _activity   = $('#activity');
var _expression = $('#expression');
var _params     = $('#parameters');
var _btnSave    = $('#btnSave');

//Functions

function _initPageApp(){
  
  _btnAdd.on('click', function(e){
    doAdd();
  });
  
  _btnReload.on('click', function(e){
    reload();
  });
  
  _btnSave.on('click', function(e){
    doSave();
  });
  
  _dialog.on('shown.bs.modal', function(e){
    onDlgShow();
  });
  
  loadData();
  
}

function loadData(){
  $.ajax({
    type: "GET",
    url: "/wcron/scheduler/manager/listJobs"
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
  _activity.focus();
}

function doAdd(){
  _activity.val('');
  _expression.val('');
  _params.val('');
  
  _dialog.data('op','add');
  _dialog.modal('show');
}

function doRemove(i){
  var c=confirm('Are you sure to remove the job?');
  if(!c) return;
  
  var r=_table.records[i];
  
  $.ajax({
    type: "GET",
    url: "/wcron/scheduler/manager/removeJob/" + r['id']
  }).then(function(res){
    if(!res) {
      alert('Activity not removed.');
      return;
    }
    reload();
  }).fail(function() {
    alert('An error has occurred.');
  });
}

function doSave(){
  var dlgop      = _dialog.data('op');
  var activity   = _activity.val();
  var expression = _expression.val();
  var params     = _params.val();
  
  console.log('op=' + dlgop + ',activity=' + activity + ',expression=' + expression);
  
  if(!activity) {
    alert('Invalid activity name');
    _activity.focus();
    return;
  }
  if(!expression) {
    alert('Invalid expression');
    _expression.focus();
    return;
  }
  
  $.ajax({
    type: "POST",
    url: "/wcron/scheduler/manager/schedule/" + activity + "/" + expression.replace(/\s/g, '_'),
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    data: JSON.stringify(_jsonToObj(params))
  }).then(function(res){
    if(!res) {
      alert('Job not scheduled.');
      return;
    }
    reload();
  }).fail(function(){
    alert('An error has occurred.');
  });
  
  _dialog.modal('hide');
}

// GUI Objects

function TableJobs(id){
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
TableJobs.prototype.onSelection=function(e){
  console.log('onSelection selIndex=' + this.selIndex);
}
TableJobs.prototype.onDblClick=function(e){
  console.log('onDblClick selIndex=' + this.selIndex);
}
TableJobs.prototype.clear=function(){
  this.records=[];
  this.selIndex=-1;
  $(this.idtable+' tbody').html('');
}
TableJobs.prototype.clearSelection=function(){
  this.selIndex=-1;
  $(this.idtable+' tbody tr').removeClass('success');
}
TableJobs.prototype.setData=function(data){
  if(!$.isArray(data)){
    this.records=[];
    $(this.idtable+' tbody').html('');
    return;
  }
  this.records=data;
  this.refresh();
}
TableJobs.prototype.refresh=function(){
  var rows='';
  for(var i=0;i<this.records.length;i++){
    var r=this.records[i];
    rows+='<tr>';
    rows+='<td>'+r['id']+'</td>';
    rows+='<td>'+r['activity']['name']+'</td>';
    rows+='<td>'+r['expression']+'</td>';
    rows+='<td>'+_formatDateTime(r['createdAt'])+'</td>';
    rows+='<td>'+(r['running'] ? '&#10004;' : '')+'</td>';
    rows+='<td>'+_formatDateTime(r['lastExecution'])+'</td>';
    rows+='<td>'+r['lastResult']+'</td>';
    rows+='<td>'+r['lastError']+'</td>';
    rows+='<td>'+r['elapsed']+'</td>';
    // Row actions
    rows+='<td><div class="btn-group">';
    rows+='<button class="btn btn-xs btn-default" style="margin-right: 4px;"  onclick="doRemove(' + i + ')">Remove</button>';
    rows+='</div></td>'; 
    rows+='</tr>';
  }
  $(this.idtable+' tbody').html(rows);
}

