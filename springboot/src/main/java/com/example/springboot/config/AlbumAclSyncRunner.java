package com.example.springboot.config;

import com.example.springboot.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.album.sync-acl-on-startup", havingValue = "true")
public class AlbumAclSyncRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AlbumAclSyncRunner.class);
    private final AlbumService albumService;

    public AlbumAclSyncRunner(AlbumService albumService) {
        this.albumService = albumService;
    }

    @Override
    public void run(ApplicationArguments args) {
        int count = albumService.syncAllObjectAcls();
        log.info("相册COS对象ACL同步完成，共处理 {} 张照片；请关闭 ALBUM_SYNC_ACL_ON_STARTUP", count);
    }
}
