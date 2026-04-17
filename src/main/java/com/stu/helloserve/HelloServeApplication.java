package com.stu.helloserve;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stu.helloserve.mapper")
public class HelloServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloServeApplication.class, args);
    }

}
