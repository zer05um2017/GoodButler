package com.j2d2.graph

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GraphDao {

    /**
     * 인슐린 & 사료급여 데이터만 조회(Only for Scatter Graph)
     * @param date (ex. 2020-06-23)
     * @return List<GraphTimeLine>
     * */
    @Query("SELECT * FROM (\n" +
            "SELECT data_type, millis, total_capacity FROM insulin WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            "UNION\n" +
            "SELECT data_type, millis, total_capacity FROM feeding WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            ")ORDER BY millis ASC")
    fun timeLineData(today:String):List<GraphTimeLine>

    /** 입력받은 년월을 기준으로 해당 월에 대한 저장된 데이터의 일자 목록 조회
     * @param 202006
     * @return [2020-06-24, 2020-06-23] 해당포맷이 코드에서 날짜 변환하기에 편함
     * */
    @Query("SELECT DT FROM (\n" +
            "SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE strftime(\"%Y%m\",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') LIKE :month\n" +
            "UNION\n" +
            "SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM insulin WHERE  strftime(\"%Y%m\",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') LIKE :month\n" +
            "UNION\n" +
            "SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM bloodglucose WHERE  strftime(\"%Y%m\",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') LIKE :month\n" +
            ") ORDER BY DT DESC")
    fun getDayListOfMonth(month:String):List<String>

    /** 각 테이블에 저장된 데이터에서 년월 목록을 내림차순으로 모두 가져온다.
     * @return [202007, 202006] */
    @Query("\n" +
            "SELECT strftime(\"%Y%m\",date(date(day/1000,'unixepoch','localtime'), '-1 month'),'localtime') as DT FROM (\n" +
                "SELECT millis as day FROM feeding\n" +
                "UNION\n" +
                "SELECT millis as day FROM insulin\n" +
                "UNION\n" +
                "SELECT millis as day FROM bloodglucose\n" +
            ") GROUP BY DT ORDER BY day DESC ")
    fun getAllMonthsList():List<String>

//    @Query("SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE strftime('%Y-%m', DT) LIKE :month GROUP BY DT ORDER BY DT DESC")
//    fun dayListOfMonth(month:String):String


    /*
    * 1. 그래프 화면 진입 시 존재하는 데이터에서 년월 목록을 가져온다.
2. 위 목록에서 해당 최근 월에 속하는 일자의 모든 데이터를 각 테이블로 부터 가져와서 그래프에 출력한다.

SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE  strftime("%Y%m",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') LIKE '202006' GROUP BY DT ORDER BY DT DESC


SELECT strftime("%Y%m",date(date(day/1000,'unixepoch','localtime'), '-1 month'),'localtime') as DT FROM (
SELECT MAX(millis) as day FROM feeding
UNION
SELECT MAX(millis) as day FROM insulin
UNION
SELECT MAX(millis) as day FROM bloodglucose
) GROUP BY DT ORDER BY day DESC --LIMIT 1

-- SELECT * FROM bloodglucose WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE '2020-06-23' ORDER BY millis

-- SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as dt FROM bloodglucose WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') GROUP BY dt ORDER BY dt DESC

--SELECT datetime(1092941466, 'unixepoch');

--SELECT datetime(1092941466, 'unixepoch', 'localtime');


-- select strftime("%Y%m",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') FROM feeding

-- date(date(millis/1000,'unixepoch','localtime'), '-1 month')


SELECT day as day from (
SELECT strftime('%Y%d, millis FROM feeding WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE '202006'
UNION
SELECT millis FROM insulin  WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE '202006'
UNION
SELECT millis FROM bloodglucose WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE '202006'
)


SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE strftime('%Y%d',DT) like '2020-06' GROUP BY DT*/
}