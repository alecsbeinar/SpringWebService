package com.example.webservice.operations;

import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.models.CalculatingResultModel;
import com.example.webservice.models.TriangleMemberStatistic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class TriangleResultAggregatorOperation {
    public static Collection<TriangleMemberStatistic> Aggregate(
            Collection<TriangleEntity> entities, Collection<CalculatingResultModel> results)
    {
        var stats = new ArrayList<TriangleMemberStatistic>();
        var areaResults = results.stream().parallel().map(res -> res.area).collect(Collectors.toCollection(ArrayList::new));
        var perimeterResults = results.stream().parallel().map(res -> res.perimeter).collect(Collectors.toCollection(ArrayList::new));

        var side1 = entities.stream().parallel().map(side -> side.A).collect(Collectors.toCollection(ArrayList::new));
        var average1 = side1.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

        var side2 = entities.stream().parallel().map(side -> side.B).collect(Collectors.toCollection(ArrayList::new));
        var average2 = side2.stream().reduce(Double::sum).get() / side2.size();

        var side3 = entities.stream().parallel().map(side -> side.C).collect(Collectors.toCollection(ArrayList::new));
        var average3 = side1.stream().reduce(Double::sum).get() / side3.size();

        var averageArea = areaResults.stream().reduce(Double::sum).get() / areaResults.size();
        var averagePerimeter = perimeterResults.stream().reduce(Double::sum).get() / perimeterResults.size();

        stats.add(new TriangleMemberStatistic("side1", Collections.min(side1), average1, Collections.max(side1)));
        stats.add(new TriangleMemberStatistic("side2", Collections.min(side2), average2, Collections.max(side2)));
        stats.add(new TriangleMemberStatistic("side3", Collections.min(side3), average3, Collections.max(side3)));
        stats.add(new TriangleMemberStatistic("area", Collections.min(areaResults), averageArea, Collections.max(areaResults)));
        stats.add(new TriangleMemberStatistic("perimeter", Collections.min(perimeterResults), averagePerimeter, Collections.max(perimeterResults)));

        return stats;
    }
}
