package day03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ResultMeta {
	public static void main(String[] args) throws Exception {
		// 드라이버 로딩
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url ="jdbc:oracle:thin:@localhost:1521:xe";
		
		Connection conn = DriverManager.getConnection(url, "scott", "1234");
		System.out.println("접속 성공");
		
		Statement stmt= conn.createStatement();
		String sql = "select * from emp";
		
		ResultSet rs=stmt.executeQuery(sql);
		
		// 메타데이터를 얻어오는 객체 --> ResultSetMetaData : 컬럼갯수, 컬럼명, 컬럼타입
		ResultSetMetaData rmd=rs.getMetaData();
				
		int colCnt = rmd.getColumnCount();
		System.out.println("컬럼갯수 : " + colCnt);
		
		// 컬럼명
		for(int i = 1; i <= colCnt; i++) {
			String colName = rmd.getColumnName(i);
			int cType = rmd.getColumnType(i);
			System.out.println("컬럼명 : " + colName + ", 컬럼타입 : "+cType);
		}
		System.out.println("===========");
		System.out.println("컬럼 사이즈 구하기");
		for(int i = 1; i <= colCnt; i++) {
			int cSize = rmd.getColumnDisplaySize(i);
			System.out.println(i + "번째 컬럼 사이즈 : " + cSize);
		}
	}
}
