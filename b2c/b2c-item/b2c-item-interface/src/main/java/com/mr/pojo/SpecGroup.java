package com.mr.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Data
@Table(name="tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)//自增策略
    private Long id;
    private Long cid;
    private String name;
    private List<SpecParam> specParamList;


}
