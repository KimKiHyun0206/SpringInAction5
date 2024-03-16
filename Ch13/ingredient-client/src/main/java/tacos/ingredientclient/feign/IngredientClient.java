package tacos.ingredientclient.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import tacos.ingredientclient.Ingredient;

/**
 * 인터페이스인데 구현 클래스가 없는 이유는 앱 실행 시 Feign이 자동으로 구현 클래스를 생성해주기 때문이다.
 */
@FeignClient("ingredient-service")
public interface IngredientClient {

  @GetMapping("/ingredients/{id}")
  Ingredient getIngredient(@PathVariable("id") String id);

  @GetMapping("/ingredients")
  Iterable<Ingredient> getAllIngredients();

}
