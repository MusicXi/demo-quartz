<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="//unpkg.com/element-ui@2.0.9/lib/theme-chalk/index.css" />
</head>
<body>

  <!-- 先引入 Vue -->
  <script src="https://unpkg.com/vue/dist/vue.js"></script>
  <!-- 引入组件库 -->
  <script src="https://unpkg.com/element-ui/lib/index.js"></script>
<div id="app">
	<template>
	  <el-table :data="tableData" stripe style="width: 100%">
	    <el-table-column prop="date" label="日期" width="180">
	    </el-table-column>
	    <el-table-column prop="name" label="姓名" width="180">
	    </el-table-column>
	    <el-table-column prop="address" label="地址">
	    </el-table-column>
	  </el-table>
	</template>
  <div align="center">
			  <el-pagination
			      @size-change="handleSizeChange"
			      @current-change="handleCurrentChange"
			      :current-page="currentPage"
			      :page-sizes="[10, 20, 30, 40]"
			      :page-size="pagesize"
			      layout="total, sizes, prev, pager, next, jumper"
			      :total="totalCount">
			  </el-pagination>
		  </div>

</div>
<script type="text/javascript">
var Main = {
	     data() {
	      return {
	        tableData: [{
	          date: '2016-05-02',
	          name: '王小虎',
	          address: '上海市普陀区金沙江路 1518 弄'
	        }, {
	          date: '2016-05-04',
	          name: '王小虎',
	          address: '上海市普陀区金沙江路 1517 弄'
	        }, {
	          date: '2016-05-01',
	          name: '王小虎',
	          address: '上海市普陀区金沙江路 1519 弄'
	        }, {
	          date: '2016-05-03',
	          name: '王小虎',
	          address: '上海市普陀区金沙江路 1516 弄'
	        }],
	        
	      //请求的URL
	        url:'../getstu',
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
	      }
	    },
	    methods: {
	    	
	        //从服务器读取数据
			loadData: function(criteria, pageNum, pageSize){					
				this.$http.get(this.url,{parameter:criteria, pageNum:pageNum, pageSize:pageSize}).then(function(res){
            		this.tableData = res.data.studentdata;
            		this.totalCount = res.data.number;
            	},function(){
              		console.log('failed');
            	});	
			},
			   //页码变更
	        handleCurrentChange: function(val) {
	            this.currentPage = val;
	            this.loadData(this.criteria, this.currentPage, this.pagesize);
	        },
	        //每页显示数据量变更
	        handleSizeChange: function(val) {
	            this.pagesize = val;
	            this.loadData(this.criteria, this.currentPage, this.pagesize);
	        }
		}
	  }
	var Ctor = Vue.extend(Main)
	new Ctor().$mount('#app')
</script>
	
</body>
</html>
</html>