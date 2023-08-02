import { useNavigation } from "@react-navigation/native";
import React, { useState } from "react";
import Toast from "react-native-root-toast";

import UserAPI from "../../Api/memberAPI";
import { Container, ImageBox, Input, Spacer, TextButton } from "../../components/common";
import UserStorage from "../../storage/UserStorage";

const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigation = useNavigation();

  const loginHandler = () => {
    if (!email.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
      Toast.show("이메일 형식을 확인해주세요", { duration: Toast.durations.SHORT });
      return;
    } else if (password.length < 4) {
      Toast.show("비밀번호는 4자리 이상 입니다", { duration: Toast.durations.SHORT });
      return;
    }

    UserAPI.login(email, password)
      .then(res => {
        if (res.message == "Not found") {
          Toast.show("존재하지 않는 이메일 입니다", { duration: Toast.durations.SHORT });
          return;
        } else if (res.message == "Invalid password") {
          Toast.show("비밀번호가 틀렸습니다", { duration: Toast.durations.SHORT });
          return;
        } else {
          console.log(res);
          UserStorage.setUserToken(res["pubKey"], res["privKey"]);

          return UserAPI.getProfile();
        }
      })
      .then(res => {
        if (res === undefined) return;
        UserStorage.setUserProfile(res.data);
      });
  };
  return (
    <Container isFullScreen={true} style={{ flex: 1, justifyContent: "center" }}>
      <Spacer />
      <ImageBox
        source={require("../../../assets/logo_ko.png")}
        isCenter={true}
        width={300}
      ></ImageBox>

      <Spacer size={100} />
      <Container style={{ width: "100%" }} isItemCenter={true} paddingHorizontal={0}>
        <Input
          style={{ paddingVertical: "5%" }}
          paddingHorizontal={20}
          borderRadius={30}
          placeholder="E-MAIL"
          onChangeText={text => setEmail(text)}
          value={email}
        ></Input>
        <Spacer size={15} />
        <Input
          style={{ paddingVertical: "5%" }}
          paddingHorizontal={20}
          borderRadius={30}
          placeholder="PW"
          onChangeText={text => setPassword(text)}
          value={password}
          secureTextEntry={true}
        ></Input>
        <Spacer size={50} />
        <TextButton onPress={loginHandler}>로그인</TextButton>
      </Container>
      <Spacer size={30} />
      <TextButton
        backgroundColor="transparent"
        paddingVertical={16}
        onPress={() => {
          navigation.navigate("TermsSet" as never);
        }}
      >
        Don't have any account? Sign up
      </TextButton>
      <Spacer size={50} />
    </Container>
  );
};

export default Login;
