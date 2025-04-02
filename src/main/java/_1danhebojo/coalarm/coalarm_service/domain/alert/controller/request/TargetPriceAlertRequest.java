package _1danhebojo.coalarm.coalarm_service.domain.alert.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TargetPriceAlertRequest extends BaseAlertRequest {

    @Null
    private Long targetId;

    @NotNull
    @JsonProperty("target_price")
    private BigDecimal price;

    @NotNull
    @JsonProperty("percentage")
    private Integer percentage;
}
