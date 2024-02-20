package sia5;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.GenericTransformer;

@Configuration
public class FileWriterIntegrationConfig {

  @Profile("xmlconfig")
  @Configuration
  @ImportResource("classpath:/filewriter-config.xml")
  public static class XmlConfiguration {}

  @Profile("javaconfig")
  @Bean
  @Transformer(inputChannel="textInChannel",
               outputChannel="fileWriterChannel") //GenericTransformer가 textInChannel의 메시지를 받아서 fileWriterChannel로 쓰는 통합 플로우 변환기라는 것을 지정한다.
  public GenericTransformer<String, String> upperCaseTransformer() {  //GenericTransformer은 합수형 인터페이스이므로 메시지 텍스트에 toUpperCate()를 호출하는 람다를 구현할 수 있다.
    return text -> text.toUpperCase();
  }

  @Profile("javaconfig")
  @Bean
  @ServiceActivator(inputChannel="fileWriterChannel") //fileWriterChannel로부터 메시지를 받아서 FileWritingMessageHandler의 인스턴스로 정의된 서비스에 넘겨줌을 나타낸다.
  public FileWritingMessageHandler fileWriter() { //이 클래스는 메시지 핸들러이며 메시지 페이로드를 지정된 디렉터리 파일에 쓴다. 이때 파일 이름은 해당 메시지릐 file_name 헤더에 지정된 것을 사용한다.
    FileWritingMessageHandler handler =
        new FileWritingMessageHandler(new File("/tmp/sia5/files"));
    handler.setExpectReply(false);  //이 메소드를 호출하지 않으면 통합 플로우가 정상적으로 작동하더라도 응답 채널이 구성되지 않았다는 로그 메시지가 나온다.
    handler.setFileExistsMode(FileExistsMode.APPEND);
    handler.setAppendNewLine(true);   //XML 구성과 동일하게 해당 파일이 이미 있으면 기존 데이터에 덮어쓰지 않고 줄을 바꾸어 제일 끝에 추가한다.
    return handler;
  }

  //
  // DSL Configuration
  //
  //이 방법은 자바 구성 방법을 더 간소화시킬 수 있는 방법이다
  @Profile("javadsl")
  @Bean
  public IntegrationFlow fileWriterFlow() {
    return IntegrationFlows
        .from(MessageChannels.direct("textInChannel"))    //textInChannel에서 메시지를 가져온다 (인바운드 채널)
        .<String, String>transform(t -> t.toUpperCase())  //텍스트를 대문자로 바꾼다 (변환기를 선언한다)
      //.channel(MessageChannels.direct("fileWriterChannel")) //변환기를 아웃바운드 채널 어댑터와 연결하는 채널의 경우에 이 채널을 별도로 구성할 필요가 있다면, 다음과 같이 플로우 정의에서 channel() 메소드를 호출하여 해당 채널을 이름으로 참조할 수 있다.
        .handle(Files                                     //파일에 쓰는 것을 처리한다
            .outboundAdapter(new File("/tmp/sia5/files")) //파일 경로는 이와 같다.
            .fileExistsMode(FileExistsMode.APPEND)
            .appendNewLine(true))                         //파일이 이미 있다면 줄을 바꾸어 제일 끝에 추가한다.
        .get();                                           //결과를 가져온다
  }
  //위와 같은 메소드 작성 방법을 빌더라고 한다.

}
