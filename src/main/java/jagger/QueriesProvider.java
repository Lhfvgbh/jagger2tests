package jagger;

import com.google.common.io.Files;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueriesProvider implements Iterable {

    private static final Logger log = LoggerFactory.getLogger(QueriesProvider.class);

    String path;
    List<String> searchQueryFull;
    public QueriesProvider(String path){
        this.path = path;
    }

    @Override
    public Iterator iterator() {
        List<JHttpQuery> queries = new ArrayList<>();

        if(!path.equals("/response-headers")){
            queries.add(new JHttpQuery()
                    .get()
                    .responseBodyType(String.class)
                    .path(path));
        } else {
            setQueries();
            for(String query:searchQueryFull) {
                queries.add(new JHttpQuery()
                        .get()
                        .responseBodyType(String.class)
                        .path(this.path)
                        .queryParam("key",query));
            }
        }
        return queries.iterator();
    }

    private void setQueries(){
        log.debug("Starting creating queries.");
        String fileName = "text.csv";
        File file = new File(fileName);

        try {
            if (file.exists()) {
                log.info("Loading phrases from file " + fileName);
                searchQueryFull = Files.readLines(file, Charset.defaultCharset());
            }
        } catch (IOException e) {
            log.error("Exception occurred while loading queries");
            throw new RuntimeException("File not found: " + file.getAbsolutePath(), e);
        }
    }
}