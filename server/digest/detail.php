<?php
$id = $_REQUEST['id'];

$mysql = new SaeMysql();

$sql = "select * from `android_digest` where id = " . $id;

$sql = $sql . " order by id desc";

$data = $mysql->getData($sql);

if (count($data) > 0) {
    echo json_encode($data[0]);
} else {
    echo "{}";
}

$mysql->closeDb();

function decodeUnicode($str) {
    return preg_replace_callback('/\\\\u([0-9a-f]{4})/i',
        create_function( '$matches', 'return mb_convert_encoding(pack("H*", $matches[1]), "UTF-8", "UCS-2BE");' ),
        $str);
}

?>
