package com.goshop.manager.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.goshop.common.exception.PageException;
import com.goshop.common.utils.PageUtils;
import com.goshop.manager.i.StoreJoinManageService;
import com.goshop.manager.i.StoreService;
import com.goshop.manager.mapper.StoreJoinMapper;
import com.goshop.manager.pojo.StoreJoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreJoinManageServiceImpl implements StoreJoinManageService {

    private static final String verify_type_fail="fail";

    private static final String verify_type_pass="pass";

    @Autowired
    StoreJoinMapper storeJoinMapper;

    @Autowired
    StoreService storeService;

    @Override
    public PageInfo<StoreJoin> findAll(Integer curPage, Integer pageSize) {
        PageUtils.startPage(curPage,pageSize);
        List<StoreJoin> list = storeJoinMapper.findAll();
        return new PageInfo<>(list);
    }

    @Override
    public StoreJoin find(Long memberId) {
        return storeJoinMapper.selectByPrimaryKey(memberId);
    }

    @Override
    public void saveVerify(Long memberId, String verify_type, String join_message, String[] commis_rate) {
        StoreJoin storeJoin= storeJoinMapper.selectByPrimaryKey(memberId);
        storeJoin.setJoininMessage(join_message);
        boolean isStore = false;
        if(storeJoin.getJoininState().equals(storeJoinMapper.JOIN_STATIC_APPLY)) {
            if (verify_type_pass.equals(verify_type)) {
                storeJoin.setJoininState(storeJoinMapper.JOIN_STATIC_EXMINE_YES);
            } else {
                storeJoin.setJoininState(storeJoinMapper.JOIN_STATIC_EXMINE_NO);
            }
        }else{
            if (verify_type_pass.equals(verify_type)) {
                storeJoin.setJoininState(storeJoinMapper.JOIN_STATIC_YES);
                isStore=true;
            } else {
                storeJoin.setJoininState(storeJoinMapper.JOIN_STATIC_PAY_NO);
            }
        }
        storeJoinMapper.updateByPrimaryKey(storeJoin);
        if(isStore){
            try {
                storeService.openStore(storeJoin);
            }catch (Exception e){
                throw new PageException("开店失败");
            }
        }
    }
}
