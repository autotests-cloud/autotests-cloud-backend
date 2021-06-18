<?xml version='1.1' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.security.AuthorizationMatrixProperty>
      <inheritanceStrategy class="org.jenkinsci.plugins.matrixauth.inheritance.InheritParentStrategy"/>
      <permission>hudson.model.Item.Build:anonymous</permission>
      <permission>hudson.model.Item.Read:anonymous</permission>
    </hudson.security.AuthorizationMatrixProperty>
    <jenkins.model.BuildDiscarderProperty>
      <strategy class="hudson.tasks.LogRotator">
        <daysToKeep>3</daysToKeep>
        <numToKeep>10</numToKeep>
        <artifactDaysToKeep>-1</artifactDaysToKeep>
        <artifactNumToKeep>-1</artifactNumToKeep>
      </strategy>
    </jenkins.model.BuildDiscarderProperty>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.ChoiceParameterDefinition>
          <name>BROWSER</name>
          <description></description>
          <choices class="java.util.Arrays$ArrayList">
            <a class="string-array">
              <string>chrome</string>
              <string>opera</string>
              <string>firefox</string>
              <string>safari</string>
            </a>
          </choices>
        </hudson.model.ChoiceParameterDefinition>
        <jp.ikedam.jenkins.plugins.extensible__choice__parameter.ExtensibleChoiceParameterDefinition plugin="extensible-choice-parameter@1.7.0">
          <name>BROWSER_VERSION</name>
          <description></description>
          <editable>true</editable>
          <editableType>NoFilter</editableType>
          <choiceListProvider class="jp.ikedam.jenkins.plugins.extensible_choice_parameter.TextareaChoiceListProvider">
            <choiceList>
              <string>89.0</string>
              <string>88.0</string>
              <string>87.0</string>
            </choiceList>
          </choiceListProvider>
        </jp.ikedam.jenkins.plugins.extensible__choice__parameter.ExtensibleChoiceParameterDefinition>
        <hudson.model.ChoiceParameterDefinition>
          <name>BROWSER_SIZE</name>
          <description></description>
          <choices class="java.util.Arrays$ArrayList">
            <a class="string-array">
              <string>1920x1080</string>
              <string>1366x768</string>
              <string>1280x1024</string>
              <string>1280x800</string>
              <string>1024x768</string>
            </a>
          </choices>
        </hudson.model.ChoiceParameterDefinition>
        <hudson.model.ChoiceParameterDefinition>
          <name>BROWSER_MOBILE</name>
          <description></description>
          <choices class="java.util.Arrays$ArrayList">
            <a class="string-array">
              <string></string>
              <string>iPhone X</string>
              <string>iPad Pro</string>
              <string>Nexus 7</string>
            </a>
          </choices>
        </hudson.model.ChoiceParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>REMOTE_DRIVER_URL</name>
          <description></description>
          <defaultValue>selenoid.autotests.cloud</defaultValue>
          <trim>false</trim>
        </hudson.model.StringParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>THREADS</name>
          <description></description>
          <defaultValue>5</defaultValue>
          <trim>false</trim>
        </hudson.model.StringParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>ALLURE_NOTIFICATIONS_VERSION</name>
          <description>https://github.com/qa-guru/allure-notifications</description>
          <defaultValue>2.2.3</defaultValue>
          <trim>false</trim>
        </hudson.model.StringParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
  </properties>
  <scm class="hudson.plugins.git.GitSCM" plugin="git@4.7.1">
    <configVersion>2</configVersion>
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>%s</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/master</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
    <submoduleCfg class="empty-list"/>
    <extensions/>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <jenkins.plugins.http__request.HttpRequest plugin="http_request@1.8.27">
      <url>https://${REMOTE_DRIVER_URL}/status</url>
      <ignoreSslErrors>false</ignoreSslErrors>
      <httpMode>GET</httpMode>
      <httpProxy></httpProxy>
      <passBuildParameters>false</passBuildParameters>
      <validResponseCodes>100:399</validResponseCodes>
      <validResponseContent></validResponseContent>
      <acceptType>NOT_SET</acceptType>
      <contentType>NOT_SET</contentType>
      <outputFile></outputFile>
      <timeout>0</timeout>
      <consoleLogResponseBody>false</consoleLogResponseBody>
      <quiet>false</quiet>
      <authentication></authentication>
      <requestBody></requestBody>
      <uploadFile></uploadFile>
      <multipartName></multipartName>
      <wrapAsMultipart>false</wrapAsMultipart>
      <useSystemProperties>false</useSystemProperties>
      <customHeaders class="empty-list"/>
    </jenkins.plugins.http__request.HttpRequest>
    <hudson.plugins.gradle.Gradle plugin="gradle@1.36">
      <switches></switches>
      <tasks>clean
test
-Dbrowser=${BROWSER}
-DbrowserVersion=${BROWSER_VERSION}
-DbrowserSize=${BROWSER_SIZE}
-DbrowserMobileView=&quot;${BROWSER_MOBILE}&quot;
-DremoteDriverUrl=https://user1:1234@${REMOTE_DRIVER_URL}/wd/hub/
-DvideoStorage=https://${REMOTE_DRIVER_URL}/video/
-Dthreads=${THREADS}</tasks>
      <rootBuildScriptDir></rootBuildScriptDir>
      <buildFile></buildFile>
      <gradleName>Gradle 6.8.3</gradleName>
      <useWrapper>false</useWrapper>
      <makeExecutable>false</makeExecutable>
      <useWorkspaceAsHome>false</useWorkspaceAsHome>
      <wrapperLocation></wrapperLocation>
      <passAllAsSystemProperties>false</passAllAsSystemProperties>
      <projectProperties></projectProperties>
      <passAllAsProjectProperties>false</passAllAsProjectProperties>
    </hudson.plugins.gradle.Gradle>
  </builders>
  <publishers>
    <ru.yandex.qatools.allure.jenkins.AllureReportPublisher plugin="allure-jenkins-plugin@2.29.0">
      <configPath></configPath>
      <jdk></jdk>
      <properties/>
      <results>
        <ru.yandex.qatools.allure.jenkins.config.ResultsConfig>
          <path>build/allure-results</path>
        </ru.yandex.qatools.allure.jenkins.config.ResultsConfig>
      </results>
      <reportBuildPolicy>ALWAYS</reportBuildPolicy>
      <includeProperties>false</includeProperties>
      <disabled>false</disabled>
      <report>allure-report</report>
    </ru.yandex.qatools.allure.jenkins.AllureReportPublisher>
    <hudson.plugins.postbuildtask.PostbuildTask plugin="postbuild-task@1.9">
      <tasks>
        <hudson.plugins.postbuildtask.TaskProperties>
          <logTexts>
            <hudson.plugins.postbuildtask.LogProperties>
              <logText></logText>
              <operator>AND</operator>
            </hudson.plugins.postbuildtask.LogProperties>
          </logTexts>
          <EscalateStatus>false</EscalateStatus>
          <RunIfJobSuccessful>false</RunIfJobSuccessful>
          <script>cd ..
FILE=./allure-notifications-${ALLURE_NOTIFICATIONS_VERSION}.jar&#xd;
if [ ! -f &quot;$FILE&quot; ]; then&#xd;
   wget https://github.com/qa-guru/allure-notifications/releases/download/${ALLURE_NOTIFICATIONS_VERSION}/allure-notifications-${ALLURE_NOTIFICATIONS_VERSION}.jar&#xd;
fi&#xd;
</script>
        </hudson.plugins.postbuildtask.TaskProperties>
        <hudson.plugins.postbuildtask.TaskProperties>
          <logTexts>
            <hudson.plugins.postbuildtask.LogProperties>
              <logText></logText>
              <operator>AND</operator>
            </hudson.plugins.postbuildtask.LogProperties>
          </logTexts>
          <EscalateStatus>false</EscalateStatus>
          <RunIfJobSuccessful>false</RunIfJobSuccessful>
          <script>java \&#xd;
&quot;-Dmessenger=telegram&quot; \&#xd;
&quot;-Dbot.token=%s&quot; \&#xd;
&quot;-Dchat.id=%s&quot; \&#xd;
&quot;-Dreply.to.message.id=%s&quot; \&#xd;
&quot;-Dbuild.launch.name=%s&quot; \&#xd;
&quot;-Dbuild.env=%s&quot; \&#xd;
&quot;-Dbuild.report.link=${BUILD_URL}&quot; \&#xd;
&quot;-Dproject.name=%s&quot; \&#xd;
&quot;-Dlang=en&quot; \&#xd;
&quot;-Denable.chart=true&quot; \&#xd;
&quot;-Dallure.report.folder=./allure-report/&quot; \&#xd;
-jar ../allure-notifications-${ALLURE_NOTIFICATIONS_VERSION}.jar</script>
        </hudson.plugins.postbuildtask.TaskProperties>
      </tasks>
    </hudson.plugins.postbuildtask.PostbuildTask>
  </publishers>
    <buildWrappers>
      <hudson.plugins.timestamper.TimestamperBuildWrapper plugin="timestamper@1.12"/>
    </buildWrappers>
</project>