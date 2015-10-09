<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8">
<title>Import a android tool</title>
</head>
<body>
<form method="post" action="post.php" enctype="multipart/form-data">
<table align="center">
<tr>
<td>标题:</td>
<td><input type="text" name="title" width="100%"/></td>
</tr>
<tr>
<td>简介:</td>
<td><textarea rows="10" cols="100" name="description">
</textarea></td>
</tr>
<tr>
<td>图片:</td>
<td><textarea rows="10" cols="100" name="thumbnail">
</textarea></td>
</tr>
<tr>
<td>官网:</td>
<td><textarea rows="10" cols="100" name="homepage">
</textarea></td>
</tr>
<tr>
<td>Apk下载地址:</td>
<td><textarea rows="10" cols="100" name="apk">
</textarea></td>
</tr>
<tr>
<td>网址:</td>
<td><textarea rows="2" cols="100" name="url">
</textarea></td>
</tr>
<tr>
<td>分类:</td>
<td>
<select name="type">
<option value="compoment">控件</option>
<option value="library">类库</option>
<option value="tool">工具</option>
<option value="code">代码</option>
<option value="project">项目</option>
</select>
</td>
</tr>
<tr>
<th colspan="2">
<input type="submit" value="提交"/>
</th>
</tr>
</table>
</form>
</body>
</html>
