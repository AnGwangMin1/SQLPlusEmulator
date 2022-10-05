package day03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLPlusEmulator01 {
	static Scanner sc = new Scanner(System.in);
	static Connection conn;
	static ResultSet rs;
	
	// 초기화 블럭
	static {
		System.out.println("SQL*Plus: Release 11.2.0.2.0");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버를 찾을 수 없습니다!!");
			System.out.println("프로그램 종료~~");
			System.exit(0);
		}
	}
	
	public static void dbConn() throws SQLException {
		String url ="jdbc:oracle:thin:@localhost:1521:xe";
		String user = "scott";
		String pwd = "1234";
		
		conn = DriverManager.getConnection(url, user, pwd);
		System.out.println("오라클 접속 성공");
	}
	
	public static void main(String[] args) throws Exception {
		dbConn();
		
		// 입력받기 스트림 객체 생성
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("SQL> ");
		String sql = "";
		
		Statement stmt =conn.createStatement();
		
		
//		sql = br.readLine();
//		System.out.println(sql);
		
		while((sql = br.readLine()) != null) { // 입력값이 null 아니면
			
			//sql.indexOf(";") -> sql문장에 ;이 없으면 -1값을 리턴
			if(sql.indexOf(";") != -1) { // sql 입력문장에 ;이 있는 경우
				// ;(세미콜론)을 제외한 문장만 잘라옴
				sql = sql.substring(0, sql.indexOf(";"));
			}
			
			if(sql.equalsIgnoreCase("exit")) {
				System.out.println("프로그램 종료!!");
				System.exit(0);
			}
			
			if(sql.equals("")) {
				System.out.print("SQL> ");
				continue;
			}else {
				
				try {
					// execute() : select명령문인지 아닌지 판단(리턴값이 resultSet객체인지 아닌지 판단)
					// true면 select문, false이면 insert, delete, update문
					boolean isRs = stmt.execute(sql.trim());
					
					if(isRs) { //ResultSet 생성 - select문일 경우
						rs = stmt.getResultSet(); // ResultSet 객체 얻어오기
						ResultSetMetaData rmd =rs.getMetaData();
						int colCnt = rmd.getColumnCount();
	//					System.out.println("colCnt : " + colCnt);
						// 컬럼별 사이즈
	//					int colSize = 0; // 각 컬럼의 해당 사이즈를 저장할 수 없음
						
						// 배열로 선언해서 각 컬럼의 사이즈를 담아준다.
						int[] colSize = new int[colCnt];  
						
						int totColSize = 0;
						String cName = null;
						
						
						// 컬럼명 출력
						for(int i =0; i<colCnt; i++) {
							// 컬럼은 1번부터 시작
							cName = rmd.getColumnName(i+1);
							// 각 컬럼사이즈 저장 불가
	//						colSize = rmd.getColumnDisplaySize(i+1)+4;
							// 각 컬럼에 해당하는 컬럼사이즈를 배열에 저장
							colSize[i] = rmd.getColumnDisplaySize(i+1)+4;
	//						System.out.print(" "+cName);
							System.out.printf("%"+colSize[i]+"s", cName);
	//						System.out.print(colSize);
							totColSize +=colSize[i];
						}
						
						System.out.println();
						
						// line 생성
						for(int i =0; i<totColSize; i++) {
							System.out.print("─");
						}
						
						System.out.println();
						
						// 각 row값을 출력
						while(rs.next()) {
							for(int i =0; i<colCnt; i++) {
								if(i==4) {
									Date rowVal = rs.getDate(i+1);
	//								System.out.print(" "+rowVal);
									System.out.printf("%"+colSize[i]+"s", rowVal);
								}else {
									String rowVal = rs.getString(i+1);
	//								System.out.print(" "+rowVal);
									System.out.printf("%"+colSize[i]+"s", rowVal);
								}
								
							}
							System.out.println();
						} // while문 
						
						
					}else { // select문이 아닐 경우
						// 수정된 행의 갯수를 리턴
						int cnt = stmt.getUpdateCount();
						String str = (cnt > 0)? cnt + "개의 레코드가 변경되었음":"변경된 레코드 없음";
						System.out.println(str);
					} // if문
				} catch(Exception e) {
					System.out.println("알 수 없는 명령입니다......");
					e.printStackTrace();
				}
				
			}// if문
			
			System.out.print("SQL> ");
		} // while문
		
	}
}
