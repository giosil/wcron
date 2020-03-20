var _table   = new TableActivities('#tabResult');

var _btnAdd  = $('#btnAdd');

var _dialog  = $('#dlgAct');

var _name    = $('#name');
var _uri     = $('#uri');
var _params  = $('#parameters');
var _btnSave = $('#btnSave');

function _initPageApp(){
	
	// Load data
	$.ajax({
		
		type: "GET",
		url: "/wcron/scheduler/manager/listActivities"
	
	}).then(function(data) {
	
		_table.setData(data);
	
	}).fail(function() {
		
		alert('An error has occurred.');
		
	});
	
	_btnAdd.on('click', function(e){
		_name.val('');
		_uri.val('');
		_params.val('');
		
		_dialog.data('op','add');
		_dialog.modal('show');
	});
	
	_btnSave.on('click', function(e){
		var dlgop = _dialog.data('op');
		
		var name  = _name.val();
		var uri   = _uri.val();
		var parms = _params.val();
		
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
		
		var record = {"name": name, "uri": uri, "parameters": _getParameters(parms)};
		
		$.ajax({
			
			type: "POST",
			url: "/wcron/scheduler/manager/addActivity",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			data: JSON.stringify(record)
		
		}).then(function(res) {
			if(res) {
				record['createdAt'] = new Date();
				_table.addRecord(record);
			}
			else {
				alert('Activity not added.');
			}
		
		}).fail(function() {
			
			alert('An error has occurred.');
			
		});
		
		_dialog.modal('hide');
	});
	
	_dialog.on('shown.bs.modal', function(e){
		_name.trigger('focus')
	});
	
}

function TableActivities(id){
	this.records=[];
	this.idtable=id;
	this.selIndex=-1;
	var _this=this;
	
	$(this.idtable).on('click', 'tbody tr', function(event){
		$(this).addClass('success').siblings().removeClass('success');
		var index=$(this).index();
		if(_this.selIndex!=index){
			_this.selIndex=index;
			_this.onSelection();
		}
	});
}
TableActivities.prototype.onSelection=function(){
	if(this.selIndex<0)return;
	console.log('TableActivities.onSelection selIndex=' + this.selIndex);
}
TableActivities.prototype.clear=function(){
	this.records=[];
	this.selIndex=-1;
	$(this.idtable+' tbody').html("");
}
TableActivities.prototype.clearSelection=function(){
	this.selIndex=-1;
	$(this.idtable+' tbody tr').removeClass('success');
}
TableActivities.prototype.setData=function(data){
	if(!$.isArray(data)){
		this.records=[];
		$(this.idtable+' tbody').html("");
		return;
	}
	this.records=data;
	this.refresh();
}
TableActivities.prototype.addRecord=function(record){
	if(record == null) return;
	if(this.records == null) this.records=[];
	this.records.push(record);
	this.refresh();
}
TableActivities.prototype.refresh=function(){
	if(this.records == null) this.records=[];
	var rows='';
	for(var i=0;i<this.records.length;i++){
		var r=this.records[i];
		rows+='<tr>';
		rows+='<td>'+r['name']+'</td>';
		rows+='<td>'+r['uri']+'</td>';
		rows+='<td>'+_formatDateTime(r['createdAt'])+'</td>';
		rows+='</tr>';
	}
	$(this.idtable+' tbody').html(rows);
}

function _formatDateTime(v) {
	if(v == null || v == 0) return '';
	if (typeof v == 'string') {
		return v;
	}
	var d;
	if (v instanceof Date) {
		d = v;
	}
	if (typeof v == 'number') {
		d = new Date(v);
	}
	if(d == null) return '';
	var m = d.getMonth() + 1;
	var sm = m < 10 ? '0' + m : '' + m;
	var sd = d.getDate() < 10 ? '0' + d.getDate() : '' + d.getDate();
	var sh = d.getHours() < 10 ? '0' + d.getHours() : '' + d.getHours();
	var sp = d.getMinutes() < 10 ? '0' + d.getMinutes() : '' + d.getMinutes();
	return sd + '/' + sm + '/' + d.getFullYear() + ' ' + sh + ':' + sp;
}

function _getParameters(v) {
	if(v == null) return {};
	v = v.trim();
	if(v.length < 7) return {};
	if(v.charAt(0) != '{') return {};
	if(v.charAt(v.length-1) != '}') return {};
	var res = null;
	try {
		res = JSON.parse(v);
	}
	catch(err) {
		alert('Invalid parameters.');
	}
	if(res == null) return {};
	return res;
}
