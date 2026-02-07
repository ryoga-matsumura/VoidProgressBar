package com.example.VoidProgressBar.Controller;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class VoidController {

  // 嘘ログのネタ帳を作る
  private static final List<String> LOGS = List.of(
      "サーバーを冷却中...",
      "量子ビットのもつれを解消中...",
      "AIの機嫌を伺っています...",
      "インターネットの重さを計測中...",
      "乱数を手動で生成中...",
      "余分なスペースを削除中...",
      "カフェインを注入中...",
      "ダークマターを生成中..."
  );

  // 作業用スレッド
  private final ExecutorService executor = Executors.newCachedThreadPool();

  // ブラウザが「/api/void-start」にアクセスしたら動く
  @GetMapping("/api/void-start")
  public SseEmitter start() {
    // ブラウザへのパイプを作る
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    //別のスレッドで作業開始
    executor.execute(() -> {
      try {
        Random random = new Random();
        int current = 0;

        // 100%未満の場合はずっと繰り返す
        while (current < 100) {
          // 進捗の計算処理
          int step = random.nextInt(10) + 1;
          current += step;
          if (current > 100)
            current = 100;

          // 進捗をブラウザにJSON形式で送る
          String json = String.format("{\"percent\": %d, \"message\": \"\"}", current);
          emitter.send(json);

          // 30%の確率で嘘ログを表示
          if (random.nextInt(10) < 3) {
            String log = LOGS.get(random.nextInt(LOGS.size()));
            // ログがあるパターンのJSONを送る
            String logJason = String.format("{\"percent\": %d, \"message\": \"%s\"}", current, log);
            emitter.send(logJason);
          }

          // 待機時間もランダムにする
          int sleepTime = random.nextInt(700) + 100;
          Thread.sleep(sleepTime);
        }

        // 完了通知
        emitter.send("{\"percent\": 100, \"message\": \"完了！\"}");
        emitter.complete(); // 通信終了

      } catch (Exception e) {
        emitter.completeWithError(e);
      }
    });
    return emitter;
  }
}
