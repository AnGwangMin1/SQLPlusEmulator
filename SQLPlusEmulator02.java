package day03;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLPlusEmulator02 {
   static Scanner sc = new Scanner(System.in);
   static Connection conn;
   static ResultSet rs;
   
   static {
      System.out.println("SQL*Plus : Release 11");
      try {
         Class.forName("oracle.jdbc.driver.OracleDriver");
      } catch (ClassNotFoundException e) {
         System.out.println("드라이버를 찾을 수 없습니다!!!");
         System.out.println("프로그램 종료~~");
         System.exit(0);
      }
   }
   
   static public void dbConn() throws SQLException {
      String url="jdbc:oracle:thin:@localhost:1521:xe";
      String user =readEntry("사용자명 입력 :");
  	  String pwd =readEntry("비밀번호 입력 :");
      
      conn = DriverManager.getConnection(url, user, pwd);
      System.out.println("오라클 데이터베이스에 접속됨~~");
   }
   
   static public String readEntry(String prompt){
   	System.out.print(prompt);
   	String sql = sc.nextLine();
   	return sql.trim();
   }
   
   public static void main(String[] args) throws Exception {
      dbConn();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("SQL> ");
      String sql = "";
      
      Statement stmt = conn.createStatement();
//      
//      입력값이 null 아니면
      while((sql = br.readLine()) != null) {
         if(sql.indexOf(";") != -1) { // ;이 있을 경우
            sql = sql.substring(0, sql.indexOf(";"));
         }else { // ;이 없을 경우
            if(sql.equals("")) { // 입력값없이 엔터키가 입려되었을 때
               System.out.print("SQL> ");
               continue;
            }else if(sql.equalsIgnoreCase("exit")) { // ;없이 exit가 입력되었을 경우
               System.out.println("Disconnected from Oracle Database 11g Express Edition Release 11.2.0.2.0 - 64bit Production");
               System.exit(0);
            }else {
               int i = 2;
               String sql2 ="";
               while(true) {
                  System.out.print(i + " ");
                  sql2 = br.readLine();
                  sql +=" "+ sql2;
                  
                  if("".equals(sql2) || sql2.indexOf(";") != -1) {
                     break;
                  }
                  i++;
               }//while
               
               if(sql.indexOf(";") != -1) {
                  sql = sql.substring(0, sql.indexOf(";"));
               }else {
                  System.out.print("SQL> ");
                  continue;
               }
            }//if
         }// if
         
        // sql문이 select절이 true 아니면 false를 리턴한다.            
        // sql문은 실행된다.            
        try {
           boolean isRs = stmt.execute(sql.trim());
           
           if(isRs) { // sql이 select문일 경우
              rs = stmt.getResultSet();                  
              // 결과 출력
              printResult(rs);                  
           }else { // sql이 select절이 아닌 경우
              int cnt = stmt.getUpdateCount();
              String[] sqlCommand = sql.split(" ");

              System.out.println("\n"+ cnt + " rows " + sqlCommand[0].toLowerCase()+"d.");
           } // if
           
        } catch (Exception e) {
           System.out.println("구문 오류 ... ... ...");
           e.printStackTrace();
        } // catch
         
        System.out.print("SQL> ");
      }// while
   }// main
   
   // 결과 출력 메소드
   public static void printResult(ResultSet rs) throws SQLException {
      ResultSetMetaData md = rs.getMetaData();
      int colCnt = md.getColumnCount();

      String cName = null;   
//      각컬럼별 사이즈를 보관하기 위해서는 배열로선언      
      int[] colSize = new int[colCnt];
      int totColSize = 0;
      
      // 컬럼명 출력
      for(int i =0; i<colCnt; i++) {
         cName = md.getColumnName(i+1);

         // 컬럼사이즈가 작은경우 구분하기 위해 넉넉히 조절
         colSize[i] = md.getColumnDisplaySize(i+1)+4;
         System.out.printf("%"+colSize[i]+"s", cName);
         totColSize += colSize[i];
      }      
      System.out.println();
      // 라인 출력
      for(int i =0; i < totColSize; i++) {
         System.out.print("─");
      }      
      
      System.out.println();
      
      // 각 레코드값 출력하기
      printRows(rs, colCnt, colSize);
   }
   
   public static void printRows(ResultSet rs, int colCnt, int[] colSize) throws SQLException {
      int cnt = 0;
      while(rs.next()) {
         // 행별 모든 컬럼 출력
         for(int i =0; i<colCnt; i++) {
            // hireDate컬럼의 데이터 타입은 Date로 얻어온다.
            if(i == 4) {                        
               Date val = rs.getDate(i+1);
               System.out.printf("%"+colSize[i]+"s", val);
            }else {
               String val = rs.getString(i+1);
               System.out.printf("%"+colSize[i]+"s", val);
            }
         }// for문
         cnt++;
         System.out.println();
      }// while
      
      System.out.println("\n"+cnt + " rows selected.");
      
   }
} // class












