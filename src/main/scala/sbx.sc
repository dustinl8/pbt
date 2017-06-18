import java.text.SimpleDateFormat

val src_format = new SimpleDateFormat("dd-MMM-YYYY")
val dst_format = new SimpleDateFormat("yyyyMMMdd")
val date_string = "12-JUN-2017"
val date_id = dst_format.format(src_format.parse(date_string))