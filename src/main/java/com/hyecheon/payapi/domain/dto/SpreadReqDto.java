package com.hyecheon.payapi.domain.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SpreadReqDto {
    @NotNull
    @Min(value = 1, message = "금액은 1원 이상을 보내야 합니다.")
    private BigInteger money;
    @NotNull
    @Min(value = 1, message = "사람 수는 1명 이상을 지정 해야 합니다.")
    private Long peopleCount;

    public SpreadReqDto(@NotNull @Min(value = 1, message = "금액은 1원 이상을 보내야 합니다.") Long money, @NotNull @Min(value = 1, message = "사람 수는 1명 이상을 지정 해야 합니다.") Long peopleCount) {
        this.money = BigInteger.valueOf(money);
        this.peopleCount = peopleCount;
    }

    public SpreadReqDto(@NotNull @Min(value = 1, message = "금액은 1원 이상을 보내야 합니다.") BigInteger money, @NotNull @Min(value = 1, message = "사람 수는 1명 이상을 지정 해야 합니다.") Long peopleCount) {
        this.money = money;
        this.peopleCount = peopleCount;
    }

    public SpreadReqDto() {
    }

    public BigInteger getMoney() {
        return money;
    }

    public void setMoney(BigInteger money) {
        this.money = money;
    }

    public Long getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Long peopleCount) {
        this.peopleCount = peopleCount;
    }
}
