<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8">
<title>New a android digest</title>
</head>
<body>
<form method="post" action="post.php" enctype="multipart/form-data">
<table align="center">
<tr>
<td>标题:</td>
<td><input type="text" name="title"/></td>
</tr>
<tr>
<td>摘要:</td>
<td><textarea rows="10" cols="100" name="abstract">
</textarea></td>
</tr>
<tr>
<td>内容:</td>
<td><textarea rows="10" cols="100" name="content">
</textarea></td>
</tr>
<tr>
<td>图片:</td>
<td><textarea rows="10" cols="100" name="thumbnail">
</textarea></td>
</tr>
<tr>
<td>网址:</td>
<td><textarea rows="2" cols="100" name="url">
</textarea></td>
</tr>
<tr>
<td>类型:</td>
<td>
<select name="type">
<option value="html">网页（html）</option>
<option value="text">文本（text）</option>
</select>
</td>
</tr>
<tr>
<td>是否显示更多:</td>
<td>
<select name="more">
<option value="0">否</option>
<option value="1">是</option>
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
