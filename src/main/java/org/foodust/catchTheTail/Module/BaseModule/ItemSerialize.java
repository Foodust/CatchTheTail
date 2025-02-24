package org.foodust.catchTheTail.Module.BaseModule;

import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.IOException;

public class ItemSerialize {

    /**
     * ItemStack을 Base64 문자열로 변환
     */
    public String serializeItem(ItemStack item) {
        try {
            // ItemStack을 바이트 배열로 직렬화
            byte[] serialized = item.serializeAsBytes();

            // Base64로 인코딩
            return Base64Coder.encodeLines(serialized);
        } catch (Exception e) {
            throw new IllegalStateException("아이템을 직렬화하는 중 오류가 발생했습니다", e);
        }
    }

    /**
     * Base64 문자열을 ItemStack으로 변환
     */
    public ItemStack deserializeItem(String data) {
        // Base64 디코딩
        byte[] serialized = Base64Coder.decodeLines(data);
        // 바이트 배열을 ItemStack으로 역직렬화
        return ItemStack.deserializeBytes(serialized);
    }

    /**
     * 여러 ItemStack을 Base64 문자열로 변환
     */
    public String serializeItems(ItemStack[] items) {
        try {
            // 모든 아이템의 총 바이트 크기 계산
            int totalSize = 4; // 배열 길이를 저장할 4바이트
            byte[][] serializedItems = new byte[items.length][];

            for (int i = 0; i < items.length; i++) {
                serializedItems[i] = items[i].serializeAsBytes();
                totalSize += 4 + serializedItems[i].length; // 각 아이템의 길이(4바이트) + 데이터
            }

            // 결과 바이트 배열 생성
            byte[] result = new byte[totalSize];
            int position = 0;

            // 배열 길이 저장
            writeInt(result, position, items.length);
            position += 4;

            // 각 아이템 저장
            for (byte[] itemData : serializedItems) {
                writeInt(result, position, itemData.length);
                position += 4;
                System.arraycopy(itemData, 0, result, position, itemData.length);
                position += itemData.length;
            }

            // Base64로 인코딩
            return Base64Coder.encodeLines(result);
        } catch (Exception e) {
            throw new IllegalStateException("아이템 배열을 직렬화하는 중 오류가 발생했습니다", e);
        }
    }

    /**
     * Base64 문자열을 ItemStack 배열로 변환
     */
    public ItemStack[] deserializeItems(String data) {
        byte[] bytes = Base64Coder.decodeLines(data);
        int position = 0;

        // 배열 길이 읽기
        int length = readInt(bytes, position);
        position += 4;

        ItemStack[] items = new ItemStack[length];

        // 각 아이템 역직렬화
        for (int i = 0; i < length; i++) {
            int itemLength = readInt(bytes, position);
            position += 4;

            byte[] itemData = new byte[itemLength];
            System.arraycopy(bytes, position, itemData, 0, itemLength);
            position += itemLength;

            items[i] = ItemStack.deserializeBytes(itemData);
        }

        return items;
    }

    // 정수를 바이트 배열에 쓰는 유틸리티 메소드
    private void writeInt(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) (value >>> 24);
        bytes[offset + 1] = (byte) (value >>> 16);
        bytes[offset + 2] = (byte) (value >>> 8);
        bytes[offset + 3] = (byte) value;
    }

    // 바이트 배열에서 정수를 읽는 유틸리티 메소드
    private int readInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }
}