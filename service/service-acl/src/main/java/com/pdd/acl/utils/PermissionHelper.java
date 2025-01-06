package com.pdd.acl.utils;

import com.pdd.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
public class PermissionHelper {
    public static List<Permission> buildPermission(List<Permission> allList) {
        // 创建用于封装最终结果的list集合
        List<Permission> trees = new ArrayList<>();

        // 遍历所有菜单list集合，得到第一层数据，即pid = 0的数据
        for (Permission permission : allList) {
            // 判断是否为pid=0的数据
            if (permission.getPid() == 0) {
                permission.setLevel(1);
                // 调用方法，从第一层开始往下找
                trees.add(findChildren(permission, allList));
            }
        }

        return trees;
    }

    /**
     * 递归寻找
     *
     * @param permission 上层节点，从这里开始往下找
     * @param allList    所有的菜单
     * @return
     */
    private static Permission findChildren(Permission permission, List<Permission> allList) {

        // 初始化下一层
        permission.setChildren(new ArrayList<Permission>());
        // 遍历allList所有数据
        for (Permission it : allList) {
            // 判断当前结点是否与allList中的pid一样
            if (permission.getId().longValue() == it.getPid().longValue()) {
                int level = permission.getLevel() + 1;
                it.setLevel(level);
                if (permission.getChildren() == null) {
                    permission.setChildren(new ArrayList<>());
                }
                // 封装下一层数据
                permission.getChildren().add(findChildren(it, allList));
            }
        }
        return permission;
    }
}
