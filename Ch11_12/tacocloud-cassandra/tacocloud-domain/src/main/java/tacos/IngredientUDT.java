package tacos;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@UserDefinedType("ingredient")  //Ingredient가 Source Class이다
public class IngredientUDT {
  //id가 없는 이유는 소스 클래스인 Ingrdient의 id속성을 가질 필요가 없기 때문이다
  //사용자 정의 타입은 우리가 원하는 어떤 속성도 가질 수 있지만 테이블 정의와 똑같지 않아도 된다
  private final String name;
  private final Ingredient.Type type;
}