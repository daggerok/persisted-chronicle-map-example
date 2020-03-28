package daggerok;

import io.vavr.control.Try;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.values.Values;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Function;

interface PostalCodeRange {

  int minCode();

  void minCode(int minCode);

  int maxCode();

  void maxCode(int maxCode);
}

/**
 * see: https://github.com/OpenHFT/Chronicle-Map/blob/master/docs/CM_Tutorial.adoc#single-chroniclemap-instance-per-jvm
 */
class ChronicleMapPersistedTest {

  @Test
  void test() {

    ChronicleMapBuilder<CharSequence, PostalCodeRange> mapBuilder =
        ChronicleMapBuilder.of(CharSequence.class, PostalCodeRange.class)
                           .name("city-postal-codes-map")
                           .averageKey("Amsterdam")
                           .entries(50_000);
    System.out.println(mapBuilder);

    File mapFile = Paths.get("target", "map.db").toFile();
    System.out.println(mapFile);

    ChronicleMap<CharSequence, PostalCodeRange> map =
        Try.of(() -> mapBuilder.createOrRecoverPersistedTo(mapFile, false))
           .onFailure(throwable -> System.out.println(throwable.getLocalizedMessage()))
           .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);
    System.out.println(map);

    PostalCodeRange rangeInstance = Values.newHeapInstance(PostalCodeRange.class);
    rangeInstance.minCode(1);
    rangeInstance.maxCode(123);
    System.out.println(rangeInstance);

    map.put("Amsterdam", rangeInstance);
    System.out.println(map);
  }
}
