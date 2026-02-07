# ==========================================
# ステージ1: ビルド部屋 (Builder)
# ==========================================
# GradleとJDKが入った「大工さん」イメージを使ってビルドします。
FROM gradle:jdk21 AS builder

# コンテナ内の作業場所を決める
WORKDIR /home/gradle/project

# ソースコードを全部コンテナの中にコピーする
# (後述の .dockerignore で余計なものは省かれます)
COPY . .

# Gradleを使ってビルド！（JARファイルを作る）
# -x test は「テストをスキップ」してデプロイを早くする設定（本番では外すことも）
RUN ./gradlew build -x test --no-daemon

# ==========================================
# ステージ2: 実行部屋 (Runtime)
# ==========================================
# 実行に必要なJREだけが入った「軽量」なイメージを使います。
FROM eclipse-temurin:21-jre

# 作業場所を決める
WORKDIR /app

# ステージ1(builder)で作ったJARファイル「だけ」を、こっちの部屋に持ってくる
# ※ファイル名が変わっても大丈夫なように *.jar で指定
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Renderは「このポートで動いてね」と環境変数 PORT を渡してくるので、
# それをSpring Bootが使うように設定します。（デフォルトは8080）
ENV SERVER_PORT=${PORT:-8080}

# アプリを起動するコマンド
ENTRYPOINT ["java", "-jar", "app.jar"]