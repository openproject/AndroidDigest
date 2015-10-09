<?php
$key = $_REQUEST['key'];
$page = $_REQUEST['page'];
$size = $_REQUEST['size'];

$mysql = new SaeMysql();

$sql = "select * from `android_tool` where title like '%" . $key . "%' or description like '%" . $key . "%'";

if ($size == null) {
    $size = 20;
}
$start = 0;
$end = $size - 1;

if ($page != null) {
    $start = ($page - 1) * $size;
    $end = $page * $size;
}

$sql = $sql . " order by id desc limit " . $start . "," . $end;

$data = $mysql->getData($sql);

echo json_encode($data);

$mysql->closeDb();

function decodeUnicode($str) {
    return preg_replace_callback('/\\\\u([0-9a-f]{4})/i',
        create_function( '$matches', 'return mb_convert_encoding(pack("H*", $matches[1]), "UTF-8", "UCS-2BE");' ),
        $str);
}

?>
