<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>欢迎使用 Element</title>
  <!-- 引入样式 -->
  <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
<!-- <script src="common/js/jquery/jquery-2.1.4.js"></script> -->

  <style>
      .el-select .el-input {
        width: 110px;
      }

      .el-table .info-row {
            background: #c9e5f5;
      }   

      #top {
          background:#20A0FF;
          padding:5px;
          overflow:hidden
      }
    </style>
</head>
<body>
  <div id="test">

    
    <div id="top">			
		<span>	
				<el-button type="text" @click="add" style="color:white">添加</el-button>	
				<el-button type="text" @click="deletenames" style="color:white">批量删除</el-button>		
				<el-button type="text" @click="dialogdd" style="color:white">测试对话</el-button>	
				<el-button type="text" @click="dialogFormVisible = false" style="color:white">取 消</el-button>	
			</span>						
		</div>	
		
		
		<br/>

        <div style="margin-top:15px">
           <el-input placeholder="请输入内容" v-model="criteria" style="padding-bottom:10px;">
			  <el-select v-model="select" slot="prepend" placeholder="请选择">
			     <el-option label="jobName" value="3"></el-option>
			  </el-select>
			  <el-button slot="append" icon="search" v-on:click="search"></el-button>
		  </el-input>  		

		  <el-table
		    ref="testTable"	
		    v-loading="loading"	  
		    :data="tableData"
		    style="width:100%"
		    border
		    :default-sort = "{prop: 'jobName', order: 'ascending'}"
		    @selection-change="handleSelectionChange"	
		    @row-click="handleclick"
		    :row-class-name = "tableRowClassName"
		    >
		    <el-table-column type="selection">
		    </el-table-column>
		    <el-table-column
		      prop="schedName"
		      label="调度器"
		      sortable>
		    </el-table-column>
		    <el-table-column
		      prop="jobName"
		      label="任务名"
		      sortable
		      show-overflow-tooltip>
		    </el-table-column>
		    <el-table-column
		      prop="jobGroup"
		      label="任务组"
		      sortable>
		    </el-table-column>
		 
		    <el-table-column
		      prop="description"
		      label="任务描述"
		      sortable>
		    </el-table-column>
		    <el-table-column
		      prop="jobClassName"
		      label="任务类名"
		      sortable
		      show-overflow-tooltip>
		    </el-table-column>
	        <el-table-column label="操作">
		      <template scope="scope">
		        <el-button
		          size="small"
		          type="primary"
		          @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
		        <el-button
		          size="small"
		          type="danger"
		          @click="handleDelete(scope.$index, scope.row)">删除</el-button>
		      </template>
		    </el-table-column>
		  </el-table>
		  
		  <div align="center">
			  <el-pagination
			      @size-change="handleSizeChange"
			      @current-change="handleCurrentChange"
			      :current-page="currentPage"
			      :page-sizes="[5, 10, 20]"
			      :page-size="pagesize"
			      layout="total, sizes, prev, pager, next, jumper"
			      :total="totalCount">
			  </el-pagination>
		  </div>
		</div> 
		
		  <el-dialog title="添加任务" :visible.sync="dialogFormVisible"  >
		
		  
		    <el-form :model="form">
		    <el-form-item label="活动名称" :label-width="formLabelWidth">
		      <el-input v-model="form.description" auto-complete="off"></el-input>
		    </el-form-item>
<!-- 		    <el-form-item label="活动区域" :label-width="formLabelWidth">
		      <el-select v-model="form.region" placeholder="请选择活动区域">
		        <el-option label="区域一" value="shanghai"></el-option>
		        <el-option label="区域二" value="beijing"></el-option>
		      </el-select>
		    </el-form-item> -->
		  </el-form>
		 <div slot="footer" class="dialog-footer">
		    <el-button @click="dialogFormVisible = false">取 消</el-button>
		    <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
		  </div>
		</el-dialog>
  </div>
  

   <footer align="center">
        <p>&copy; Spring Boot Demo</p>
    </footer>
</body>
  <!-- 先引入 Vue -->
  <script src="https://unpkg.com/vue/dist/vue.js"></script>
  <!-- 引入组件库 -->
<!--   <script src="https://unpkg.com/element-ui/lib/index.js"></script> -->
  <script src="//unpkg.com/element-ui@2.0.9/lib/index.js"></script>
  
  <!-- vue-resource是vuejs的一个ajax插件，可以通过XMLHttpRequest或JSONP发起请求并处理响应 -->
  <script src="https://cdn.bootcss.com/vue-resource/1.3.4/vue-resource.js"></script>

<!-- <script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.js"></script> -->
  
  <script>
  
	var vue = new Vue({			
			el:"#test",
		    data: {		  
		    	//表格当前页数据
		    	tableData: [],
		    	loading: true,
		    	dialogFormVisible: false,
		    	form: {
		    		description: '',
		/*             region: '',
		            date1: '',
		            date2: '',
		            delivery: false,
		            type: [],
		            resource: '',
		            desc: '' */
		          },
		          formLabelWidth: '120px',
		    	
		    	//多选数组
		        multipleSelection: [],
		        
		        //请求的URL
		        url:'qrtzJobDetails/listByPage',
		        
		        //搜索条件
		        criteria: '',
		        
		        //下拉菜单选项
		        select: '',
		        
		        //默认每页数据量
		        pagesize: 10,
		        
		        //默认高亮行数据id
		        highlightId: -1,
		        
		        //当前页码
		        currentPage: 1,
		        
		        //查询的页码
		        start: 1,
		        
		        //默认数据总数
		        totalCount: 1000,
		    },
		    methods: {
		    	
		        //从服务器读取数据
				loadData: function(criteria, pageNum, pageSize){	
					debugger;

					this.$http.post(this.url,{jobName:criteria, pageNum:pageNum, pageSize:pageSize},{emulateJSON: true}).then(function(res){
                		this.tableData = res.data.studentdata;
                		this.totalCount = res.data.number;
                		this.loading = false;
                	},function(){
                  		console.log('failed');
                	});					
				},
		    	
				//多选响应
			    handleSelectionChange: function(val) {
			        this.multipleSelection = val;
			    },
			    
			    //点击行响应
			    handleclick: function(row, event, column){
			    	this.highlightId = row.id;
			    },
					
			    //编辑
				handleEdit: function(index, row) {
	/* 			    this.$prompt('请输入新名称', '提示', {
		                  confirmButtonText: '确定',
		                  cancelButtonText: '取消',
		                }).then(({ value }) => {
		                	if(value==''||value==null)
		        				return;
		        			this.$http.post('../update',{"id":row.id,"name":value},{emulateJSON: true}).then(function(res){
		        				this.loadData(this.criteria, this.currentPage, this.pagesize);	        					
		                    },function(){
		                        console.log('failed');
		                    });
		                }).catch(() => {
		            }); */
			    	this.form = row;
			      	this.dialogFormVisible = true;
		        },
		        
				      
		        //单行删除
			    handleDelete: function(index, row) {
			        var array = [];
		        	array.push(row.id);
					this.$http.post('../delete',{"array":array},{emulateJSON: true}).then(function(res){
						this.loadData(this.criteria, this.currentPage, this.pagesize);
		            },function(){
		                console.log('failed');
		            });
		        },
		        
		        //搜索
		        search: function(){
		        	this.loadData(this.criteria, this.currentPage, this.pagesize);
		        },
		        
		        //添加
		        add: function(){
		                this.$prompt('请输入名称', '提示', {
		                  confirmButtonText: '确定',
		                  cancelButtonText: '取消',
		                }).then(({ value }) => {
		                	if(value==''||value==null)
		        				return;
		        			this.$http.post('../add',{"name":value},{emulateJSON: true}).then(function(res){
		        				this.loadData(this.criteria, this.currentPage, this.pagesize);
		                    },function(){
		                        console.log('failed');
		                    });
		                }).catch(() => {
 
		            });
		              
		        },
		        
		        dialogdd: function() {
		        	debugger;
		        	this.dialogFormVisible = true;
		        },
		        
		        //多项删除
		        deletenames: function(){
		        	if(this.multipleSelection.length==0)
		        		return;
		        	var array = [];
		        	this.multipleSelection.forEach((item) => {
		        		array.push(item.id);
			        })
					this.$http.post('../delete',{"array":array},{emulateJSON: true}).then(function(res){
						this.loadData(this.criteria, this.currentPage, this.pagesize);
		            },function(){
		                console.log('failed');
		            });
		        },
		      
		        //改变当前点击的行的class，高亮当前行
		        tableRowClassName: function(row, index){
		    	   if(row.id == this.highlightId)
		    	   {
		    		  return 'info-row';
		    	   }
		        },
		      
		        //每页显示数据量变更
		        handleSizeChange: function(val) {
		            this.pagesize = val;
		            this.loadData(this.criteria, this.currentPage, this.pagesize);
		        },
		        
		        //页码变更
		        handleCurrentChange: function(val) {
		            this.currentPage = val;
		            this.loadData(this.criteria, this.currentPage, this.pagesize);
		        },	      
		        		        
		    },	    
		    
		    
		  });
	
		  //载入数据
    	  vue.loadData(vue.criteria, vue.currentPage, vue.pagesize);
	</script>  

</html>