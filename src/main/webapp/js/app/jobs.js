var _table = new TableJobs('#tabResult');

function _initPageApp(){
	
	// Load data
	$.ajax({
		
		type: "GET",
		url: "/wcron/scheduler/manager/listJobs"
	
	}).then(function(data) {
	
		_table.setData(data);
	
	}).fail(function() {
		
		alert('An error has occurred.');
		
	});
	
}

function TableJobs(id){
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
TableJobs.prototype.onSelection=function(){
	if(this.selIndex<0)return;
	console.log('TableJobs.onSelection selIndex=' + this.selIndex);
}
TableJobs.prototype.clear=function(){
	this.records=[];
	this.selIndex=-1;
	$(this.idtable+' tbody').html("");
}
TableJobs.prototype.clearSelection=function(){
	this.selIndex=-1;
	$(this.idtable+' tbody tr').removeClass('success');
}
TableJobs.prototype.setData=function(data){
	if(!$.isArray(data)){
		this.records=[];
		$(this.idtable+' tbody').html("");
		return;
	}
	this.records=data;
	var rows='';
	for(var i=0;i<this.records.length;i++){
		var r=this.records[i];
		rows+='<tr>';
		rows+='<td>'+r['id']+'</td>';
		rows+='<td>'+_getActivityName(r['activity'])+'</td>';
		rows+='<td>'+r['expression']+'</td>';
		rows+='<td>'+_formatDateTime(r['createdAt'])+'</td>';
		rows+='<td>'+_formatBoolean(r['running'])+'</td>';
		rows+='<td>'+_formatDateTime(r['lastExecution'])+'</td>';
		rows+='<td>'+r['lastResult']+'</td>';
		rows+='<td>'+r['lastError']+'</td>';
		rows+='<td>'+r['elapsed']+'</td>';
		rows+='</tr>';
	}
	$(this.idtable+' tbody').html(rows);
}

function _getActivityName(v) {
	if(v == null) return '';
	var res;
	if(typeof v == 'object') {
		res = v['name'];
	}
	if(res == null) res = '';
	return res;
}
function _formatBoolean(v) {
	if(v == null) return '';
	if(!v) return '';
	return '&#10004;';
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
