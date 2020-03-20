var _table = new TableActivities('#tabResult');

function _initPageApp(){
	
	// Load data
	$.ajax({
	
		url: "/wcron/scheduler/manager/listActivities"
	
	}).then(function(data) {
	
		_table.setData(data);
	
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
